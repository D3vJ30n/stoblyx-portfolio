package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import com.j30n.stoblyx.application.port.in.admin.AdminRankingUseCase;
import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@Primary
@Order(1)
@ActiveProfiles("test")
public class SecurityTestConfig {

    @Bean
    @Primary
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor("test_jwt_secret_key_for_testing_purposes_only".getBytes(StandardCharsets.UTF_8));
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
            
            // 테스트 인증 헤더가 있거나 Authorization 헤더가 있는 경우 인증 처리
            if (testAuthHeader != null && testAuthHeader.equals("true")) {
                UserRole role = (testRoleHeader != null && testRoleHeader.equals("ROLE_ADMIN")) 
                    ? UserRole.ADMIN : UserRole.USER;
                
                // 테스트 사용자 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = createTestAuthentication(role);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("테스트 인증 설정됨: " + authentication.getName());
            }
            else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // 테스트용 토큰 처리
                String token = authHeader.substring(7);
                
                if (token.startsWith("test_")) {
                    UserRole role = (testRoleHeader != null && testRoleHeader.equals("ROLE_ADMIN")) 
                        ? UserRole.ADMIN : UserRole.USER;
                    
                    // 테스트 사용자 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = createTestAuthentication(role);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Bearer 토큰 인증 설정됨: " + authentication.getName());
                }
            }
            
            filterChain.doFilter(request, response);
        }
    }
    
    /**
     * 테스트 환경에서 사용할 인증 객체를 생성합니다.
     */
    private UsernamePasswordAuthenticationToken createTestAuthentication(UserRole role) {
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
        
        User testUser = User.builder()
            .username("test_user")
            .password("password")
            .nickname("테스트 사용자")
            .email("test@example.com")
            .role(role)
            .build();
        
        UserPrincipal userPrincipal = UserPrincipal.builder()
            .id(1L)
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
    @Primary
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
            
            System.out.println("테스트 UserDetailsService 호출됨: " + usernameOrEmail);
            System.out.println("사용자 생성: username=" + username + ", email=" + email);
            
            User testUser = User.builder()
                .username(username)
                .password(passwordEncoder().encode("password"))
                .nickname("테스트 사용자")
                .email(email)
                .role(UserRole.USER)
                .build();
            return UserPrincipal.create(testUser);
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
}