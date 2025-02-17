package com.j30n.stoblyx.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 검증하고 인증을 처리하는 필터
 * 모든 요청에 대해 한 번만 실행되며, 유효한 JWT 토큰이 있는 경우 인증 정보를 설정합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractJwtFromRequest(request);

            if (token != null && jwtProvider.validateToken(token)) {
                // JWT에서 사용자 식별자 추출 (보안 강화)
                String userIdentifier = jwtProvider.getUserIdentifierFromToken(token);
                authenticateUser(userIdentifier);
                log.debug("Successfully authenticated user with token");
            }
        } catch (Exception e) {
            log.error("Could not authenticate user: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // 보안 컨텍스트 초기화
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출합니다.
     * Authorization 헤더에서 "Bearer " 접두사 다음에 오는 토큰을 반환합니다.
     *
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰, 토큰이 없는 경우 null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 주어진 사용자 식별자로 인증을 수행합니다.
     * UserDetails를 로드하고 인증 토큰을 생성하여 SecurityContext에 설정합니다.
     *
     * @param userIdentifier 사용자 식별자
     * @throws Exception 인증 과정에서 발생할 수 있는 예외
     */
    private void authenticateUser(String userIdentifier) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userIdentifier);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Failed to authenticate user: {}", e.getMessage());
            throw e; // 상위 메서드에서 처리하도록 예외 전파
        }
    }
}
