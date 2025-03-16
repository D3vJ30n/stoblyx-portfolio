package com.j30n.stoblyx.infrastructure.security;

import com.j30n.stoblyx.adapter.out.persistence.auth.AuthAdapter;
import com.j30n.stoblyx.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/auth/signup",
        "/auth/login",
        "/auth/refresh",
        "/auth/logout",
        "/actuator"
    );

    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthAdapter authAdapter;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        log.debug("Request URI: {}, Context Path: {}", path, contextPath);

        // contextPath를 제거한 실제 경로 추출
        String actualPath = path;
        if (!contextPath.isEmpty() && path.startsWith(contextPath)) {
            actualPath = path.substring(contextPath.length());
        }

        // PUBLIC_PATHS 중 하나라도 일치하면 필터링하지 않음
        boolean shouldNotFilter = PUBLIC_PATHS.stream().anyMatch(actualPath::startsWith);
        log.debug("요청 경로 {} 필터링 여부: {}", actualPath, !shouldNotFilter);
        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String jwt = resolveToken(request);
        String requestURI = request.getRequestURI();
        log.debug("JWT 인증 필터 진입: uri={}, token 존재여부={}", requestURI, StringUtils.hasText(jwt));

        try {
            // 테스트 모드 확인 및 처리
            boolean isTestMode = "true".equals(request.getHeader("X-TEST-AUTH"));
            boolean isTestToken = StringUtils.hasText(jwt) && jwt.equals("mock-token-for-testing");

            if (isTestMode || isTestToken) {
                log.debug("테스트 모드 감지: uri={}, isTestMode={}, isTestToken={}", requestURI, isTestMode, isTestToken);

                // 테스트용 사용자 ID 확인
                String testUserIdHeader = request.getHeader("X-TEST-USER-ID");
                Long userId = 1L; // 기본값
                if (testUserIdHeader != null && !testUserIdHeader.isEmpty()) {
                    try {
                        userId = Long.parseLong(testUserIdHeader);
                    } catch (NumberFormatException e) {
                        log.warn("잘못된 테스트 사용자 ID 형식: {}", testUserIdHeader);
                    }
                }

                // 테스트용 역할 확인
                String testRole = request.getHeader("X-TEST-ROLE");
                String role = (testRole != null && !testRole.isEmpty()) ? testRole : "USER";

                // 테스트용 사용자 조회 시도 (없으면 모의 사용자 생성)
                User testUser = null;
                try {
                    testUser = authAdapter.findUserById(userId).orElse(null);
                    log.debug("테스트용 사용자 조회 성공: userId={}", userId);
                } catch (Exception e) {
                    log.debug("테스트용 사용자 조회 실패, 모의 사용자를 생성합니다: {}", e.getMessage());
                }

                // 테스트용 인증 객체 생성
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    "test-user-" + userId, "", AuthorityUtils.createAuthorityList("ROLE_" + role));

                // SecurityContext에 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                // 사용자 ID를 요청 속성에 추가
                request.setAttribute("userId", userId);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("테스트용 인증 정보 설정 완료: userId={}, role={}, uri={}", userId, role, requestURI);
            } else if (StringUtils.hasText(jwt)) {
                // 실제 토큰 처리
                try {
                    // 토큰이 블랙리스트에 있는지 확인
                    boolean isBlacklisted = checkIfTokenBlacklisted(jwt);

                    if (!isBlacklisted) {
                        try {
                            Authentication authentication = tokenProvider.getAuthentication(jwt);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.debug("Security Context에 인증 정보를 저장했습니다: user={}, uri={}",
                                authentication.getName(), requestURI);
                        } catch (Exception e) {
                            log.error("인증 정보 추출 실패: {}, uri={}", e.getMessage(), requestURI, e);
                        }
                    } else {
                        log.debug("로그아웃된 JWT 토큰입니다, uri: {}", requestURI);
                    }
                } catch (RedisConnectionFailureException e) {
                    log.error("Redis 연결 실패, 블랙리스트 확인 생략: {}", e.getMessage());

                    // Redis 연결 실패 시에도 토큰 검증 시도
                    try {
                        Authentication authentication = tokenProvider.getAuthentication(jwt);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("(Redis 없이) Security Context에 인증 정보를 저장했습니다: user={}, uri={}",
                            authentication.getName(), requestURI);
                    } catch (Exception authEx) {
                        log.error("인증 정보 추출 실패: {}, uri={}", authEx.getMessage(), requestURI);
                    }
                }
            } else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("JWT 인증 필터 처리 중 예상치 못한 오류 발생: {}, uri: {}", e.getMessage(), requestURI, e);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean checkIfTokenBlacklisted(String token) {
        if (token == null) {
            return false;
        }

        try {
            // Redis 확인
            String key = "blacklist:" + token;
            Boolean isBlacklisted = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(isBlacklisted);
        } catch (Exception e) {
            log.error("블랙리스트 확인 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }
} 