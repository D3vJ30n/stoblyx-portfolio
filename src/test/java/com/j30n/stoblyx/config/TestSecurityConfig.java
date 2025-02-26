package com.j30n.stoblyx.config;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@Primary
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor("test_jwt_secret_key_for_testing_purposes_only".getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",
                    "/search/**",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"result\":\"ERROR\",\"message\":\"인증되지 않은 사용자입니다.\",\"data\":null}");
                })
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'"))
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

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
        return username -> {
            User testUser = User.builder()
                .username(username)
                .password(passwordEncoder().encode("password"))
                .nickname("테스트 사용자")
                .email("test@example.com")
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
}