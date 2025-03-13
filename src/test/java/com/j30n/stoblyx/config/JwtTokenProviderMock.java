package com.j30n.stoblyx.config;

import com.j30n.stoblyx.infrastructure.security.JwtTokenProvider;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 테스트 환경에서 사용할 JWT 토큰 제공자 구현
 */
@Slf4j
@TestConfiguration
@ActiveProfiles("test")
public class JwtTokenProviderMock {

    @Bean
    @Primary
    public JwtTokenProvider testJwtTokenProvider(UserDetailsService userDetailsService, SecretKey jwtSecretKey) {
        return new TestJwtTokenProvider(userDetailsService);
    }

    /**
     * 테스트용 JwtTokenProvider 구현
     * 고정된 토큰 대신 유니크한 토큰을 생성하여 인증 로직이 정상 작동하도록 함
     */
    public static class TestJwtTokenProvider extends JwtTokenProvider {
        private final UserDetailsService userDetailsService;
        private final Map<String, String> tokenToUsername = new HashMap<>();
        private final Map<String, String> blacklist = new HashMap<>();

        public TestJwtTokenProvider(UserDetailsService userDetailsService) {
            super("test_jwt_secret_key_for_testing_purposes_only", 3600, 86400, userDetailsService);
            this.userDetailsService = userDetailsService;
        }

        @Override
        public String createAccessToken(Authentication authentication) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String token = "test_access_" + UUID.randomUUID();
            tokenToUsername.put(token, userPrincipal.getUsername());
            log.info("테스트: 액세스 토큰 생성 - 사용자: {}, 토큰: {}", userPrincipal.getUsername(), token);
            return token;
        }

        @Override
        public String createRefreshToken(String username) {
            String token = "test_refresh_" + UUID.randomUUID();
            tokenToUsername.put(token, username);
            log.info("테스트: 리프레시 토큰 생성 - 사용자: {}, 토큰: {}", username, token);
            return token;
        }

        @Override
        public Authentication getAuthentication(String token) {
            String username = tokenToUsername.get(token);
            if (username == null || blacklist.containsKey(token)) {
                throw new RuntimeException("Invalid token");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }

        @Override
        public boolean validateToken(String token) {
            return tokenToUsername.containsKey(token) && !blacklist.containsKey(token);
        }

        @Override
        public String getUsername(String token) {
            return tokenToUsername.get(token);
        }

        // 테스트 환경에서 추가 메서드
        public void addToBlacklist(String token) {
            blacklist.put(token, "true");
        }
    }
} 