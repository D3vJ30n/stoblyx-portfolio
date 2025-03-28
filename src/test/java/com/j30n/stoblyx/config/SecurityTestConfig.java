package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.out.persistence.auth.AuthAdapter;
import com.j30n.stoblyx.application.port.in.admin.AdminRankingUseCase;
import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@TestConfiguration
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Primary
@Order(1)
@ActiveProfiles("test")
public class SecurityTestConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityTestConfig.class);

    @Bean
    @Primary
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor("test_jwt_secret_key_for_testing_purposes_only".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 테스트 환경에서 사용할 인증 객체를 생성합니다.
     *
     * @param role   사용자 역할
     * @param userId 사용자 ID
     * @return 인증 토큰
     */
    private UsernamePasswordAuthenticationToken createTestAuthentication(UserRole role, Long userId) {
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );

        String username = (role == UserRole.ADMIN) ? "testadmin" : "testuser";
        String email = (role == UserRole.ADMIN) ? "admin@example.com" : "test@example.com";

        User testUser = User.builder()
            .username(username)
            .password("password")
            .nickname("테스트 " + (role == UserRole.ADMIN ? "관리자" : "사용자"))
            .email(email)
            .role(role)
            .build();

        // User 객체에서는 ID를 설정하지 않고, UserPrincipal 생성 시 ID를 명시적으로 설정
        UserPrincipal userPrincipal = UserPrincipal.builder()
            .id(userId) // 명시적으로 ID 설정
            .username(testUser.getUsername())
            .email(testUser.getEmail())
            .role(testUser.getRole().name())
            .authorities(authorities)
            .build();

        return new UsernamePasswordAuthenticationToken(
            userPrincipal, null, authorities
        );
    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/contents/*/like").authenticated()
                .requestMatchers("/contents/*/bookmark").authenticated()
                .requestMatchers("/**").permitAll()
            )
            .headers(headers -> headers
                .frameOptions(FrameOptionsConfig::sameOrigin)
            )
            .addFilterBefore(new TestAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 테스트 인증 필터 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return usernameOrEmail -> {
            // 이메일 형식인 경우 이메일로 간주, 아니면 username으로 간주
            boolean isEmail = usernameOrEmail.contains("@");

            String username = isEmail ? usernameOrEmail.split("@")[0] : usernameOrEmail;
            String email = isEmail ? usernameOrEmail : username + "@example.com";

            // 관리자 계정인지 확인
            boolean isAdmin = username.equals("admin") || username.equals("testadmin");
            UserRole role = isAdmin ? UserRole.ADMIN : UserRole.USER;
            Long userId = isAdmin ? 2L : 1L;

            logger.info("테스트 UserDetailsService 호출됨: {}", usernameOrEmail);
            logger.info("사용자 생성: username={}, email={}, role={}, id={}", username, email, role, userId);

            User testUser = User.builder()
                .username(username)
                .password(passwordEncoder().encode("password"))
                .nickname("테스트 " + (isAdmin ? "관리자" : "사용자"))
                .email(email)
                .role(role)
                .build();

            // User 객체에서는 ID를 설정하지 않고, UserPrincipal 생성 시 ID를 명시적으로 설정
            return UserPrincipal.builder()
                .id(userId) // 명시적으로 ID 설정
                .username(testUser.getUsername())
                .email(testUser.getEmail())
                .role(testUser.getRole().name())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role.name())))
                .build();
        };
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public AdminRankingUseCase adminRankingUseCase() {
        return Mockito.mock(AdminRankingUseCase.class);
    }

    @Bean
    public GamificationRewardPort gamificationRewardPort() {
        return Mockito.mock(GamificationRewardPort.class);
    }

    @Bean
    public com.j30n.stoblyx.domain.repository.RankingUserActivityRepository rankingUserActivityRepository() {
        return Mockito.mock(com.j30n.stoblyx.domain.repository.RankingUserActivityRepository.class);
    }

    @Bean
    public com.j30n.stoblyx.domain.repository.RankingUserScoreRepository rankingUserScoreRepository() {
        return Mockito.mock(com.j30n.stoblyx.domain.repository.RankingUserScoreRepository.class);
    }

    /**
     * 테스트용 AuthAdapter Mock 빈
     */
    @Bean
    public AuthAdapter authAdapter() {
        AuthAdapter mockAuthAdapter = Mockito.mock(AuthAdapter.class);

        // 기본 동작 설정
        Mockito.when(mockAuthAdapter.isTokenBlacklisted(Mockito.anyString())).thenReturn(false);

        // 테스트용 사용자 생성
        User testUser = User.builder()
            .username("testuser")
            .email("test@example.com")
            .nickname("테스트 사용자")
            .role(UserRole.USER)
            .build();

        User testAdmin = User.builder()
            .username("testadmin")
            .email("admin@example.com")
            .nickname("테스트 관리자")
            .role(UserRole.ADMIN)
            .build();

        // ID 설정을 위한 리플렉션 사용
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testUser, 1L);
            idField.set(testAdmin, 2L);
        } catch (Exception e) {
            logger.error("테스트 사용자 ID 설정 실패", e);
        }

        // findUserById 메서드
        Mockito.when(mockAuthAdapter.findUserById(1L)).thenReturn(Optional.of(testUser));
        Mockito.when(mockAuthAdapter.findUserById(2L)).thenReturn(Optional.of(testAdmin));

        // findUserByEmail 메서드
        Mockito.when(mockAuthAdapter.findUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        Mockito.when(mockAuthAdapter.findUserByEmail("admin@example.com")).thenReturn(Optional.of(testAdmin));

        // 기타 메서드 설정
        Mockito.when(mockAuthAdapter.saveUser(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        logger.info("테스트용 AuthAdapter mock 빈 생성 완료");
        return mockAuthAdapter;
    }

    /**
     * 테스트용 JWT 인증 필터
     * 테스트 환경에서 특별한 JWT 토큰을 사용하여 인증을 우회합니다.
     */
    public class TestAuthenticationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            String authHeader = request.getHeader("Authorization");
            String testAuthHeader = request.getHeader("X-TEST-AUTH");
            String testRoleHeader = request.getHeader("X-TEST-ROLE");
            String testUserIdHeader = request.getHeader("X-TEST-USER-ID");
            String requestURI = request.getRequestURI();

            logger.info("테스트 인증 필터 실행: " + requestURI);

            // 테스트 모드 또는 테스트 토큰 확인
            boolean isTestMode = testAuthHeader != null && testAuthHeader.equals("true");
            boolean isTestToken = authHeader != null && authHeader.equals("Bearer mock-token-for-testing");

            if (isTestMode || isTestToken) {
                // 헤더에서 지정한 역할 사용, 지정하지 않으면 기본값으로 USER 사용
                UserRole role = UserRole.USER;

                if (testRoleHeader != null) {
                    try {
                        role = UserRole.valueOf(testRoleHeader.replace("ROLE_", ""));
                    } catch (IllegalArgumentException e) {
                        logger.warn("유효하지 않은 역할: " + testRoleHeader + ". 기본값 USER 사용");
                    }
                }

                // 사용자 ID가 지정된 경우 사용, 아니면 기본값 1L(USER) 또는 2L(ADMIN) 사용
                Long userId = (role == UserRole.ADMIN) ? 2L : 1L;
                if (testUserIdHeader != null) {
                    try {
                        userId = Long.parseLong(testUserIdHeader);
                    } catch (NumberFormatException e) {
                        logger.warn("유효하지 않은 사용자 ID: " + testUserIdHeader + ". 기본값 " + userId);
                    }
                }

                // 테스트 사용자 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = createTestAuthentication(role, userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", userId); // 사용자 ID를 요청 속성에 추가

                logger.info("테스트 인증 설정됨: 역할=" + role + ", 사용자=" + authentication.getName() +
                    ", ID=" + userId + ", URI=" + requestURI);
            } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // 일반 Bearer 토큰 처리 (테스트용 토큰이 아닌 경우)
                String token = authHeader.substring(7);

                // 테스트 환경에서는 모든 토큰을 유효한 것으로 처리
                UserRole role = UserRole.USER;
                Long userId = 1L;

                // 헤더에서 역할과 ID 추출 시도
                if (testRoleHeader != null) {
                    try {
                        role = UserRole.valueOf(testRoleHeader.replace("ROLE_", ""));
                    } catch (IllegalArgumentException e) {
                        logger.warn("유효하지 않은 역할: " + testRoleHeader + ". 기본값 USER 사용");
                    }
                }

                if (testUserIdHeader != null) {
                    try {
                        userId = Long.parseLong(testUserIdHeader);
                    } catch (NumberFormatException e) {
                        logger.warn("유효하지 않은 사용자 ID: " + testUserIdHeader + ". 기본값 " + userId);
                    }
                }

                // 테스트 사용자 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = createTestAuthentication(role, userId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", userId); // 사용자 ID를 요청 속성에 추가

                logger.info("Bearer 토큰 인증 설정됨: 역할=" + role + ", 사용자=" + authentication.getName() +
                    ", ID=" + userId + ", URI=" + requestURI);
            }

            filterChain.doFilter(request, response);
        }
    }
}