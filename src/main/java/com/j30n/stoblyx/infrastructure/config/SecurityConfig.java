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

import static org.springframework.http.HttpMethod.*;

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
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
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
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",
                    "/actuator/**",
                    "/error",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/books/search",
                    "/books/{id}"
                ).permitAll()
                .requestMatchers(GET, "/books").permitAll()
                .requestMatchers(POST, "/books").hasRole("ADMIN")
                .requestMatchers(PUT, "/books/**").hasRole("ADMIN")
                .requestMatchers(DELETE, "/books/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .build();
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