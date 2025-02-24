package com.j30n.stoblyx.infrastructure.config;

import com.j30n.stoblyx.domain.repository.UserRepository;
import com.j30n.stoblyx.infrastructure.security.CustomUserDetailsService;
import com.j30n.stoblyx.infrastructure.security.JwtAuthenticationFilter;
import com.j30n.stoblyx.infrastructure.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    private final Environment environment;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(
        Environment environment,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        RedisTemplate<String, String> redisTemplate,
        AuthenticationConfiguration authenticationConfiguration
    ) {
        this.environment = environment;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");  // 모든 Origin 허용
        configuration.addAllowedMethod("*");         // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*");         // 모든 헤더 허용
        configuration.setAllowCredentials(true);     // 인증 정보 허용
        configuration.setMaxAge(3600L);             // preflight 캐시 시간

        // Authorization 헤더 노출
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtTokenProvider tokenProvider,
        AuthenticationProvider authenticationProvider
    ) throws Exception {
        // 1. 기본 보안 설정
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider);

        // 2. H2 콘솔 설정 (테스트 환경)
        if (isTestProfile()) {
            http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        }

        // 3. URL 기반 보안 설정
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()  // 인증 관련 엔드포인트는 모두 허용
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/favicon.ico").permitAll()
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated()
        );

        // 4. JWT 필터 설정 (인증 필터 이전에 실행)
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(tokenProvider, redisTemplate);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private boolean isTestProfile() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("test")) {
                return true;
            }
        }
        return false;
    }
} 