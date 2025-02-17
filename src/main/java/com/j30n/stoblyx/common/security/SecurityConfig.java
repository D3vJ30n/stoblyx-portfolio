package com.j30n.stoblyx.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정 클래스
 * JWT 기반의 인증/인가를 처리합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // 공개 API 엔드포인트 정의
    private static final String[] PUBLIC_URLS = {
        "/api/users/register",
        "/api/users/login",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
    
    // 허용된 프론트엔드 도메인
    private static final List<String> ALLOWED_ORIGINS = List.of(
        "http://localhost:3000",  // 개발 환경
        "https://stoblyx.com"     // 운영 환경
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight 요청 허용
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * CORS 설정을 구성합니다.
     * 허용된 출처, 메서드, 헤더를 명시적으로 정의합니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // preflight 캐시 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 패스워드 인코더 빈을 구성합니다.
     * BCrypt 알고리즘을 사용하여 패스워드를 안전하게 해시화합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 