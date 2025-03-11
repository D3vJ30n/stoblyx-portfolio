package com.j30n.stoblyx.config;

import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 테스트 환경에서 사용할 AuthPort의 Mock 구현체
 * Redis 대신 메모리에 데이터를 저장합니다.
 */
@Slf4j
@TestConfiguration
public class AuthPortTestAdapter {
    
    @Bean
    @Primary
    public AuthPort authPort() {
        return new InMemoryAuthPort();
    }
    
    public static class InMemoryAuthPort implements AuthPort {
        private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
        private final Map<Long, User> usersById = new ConcurrentHashMap<>();
        private final Map<String, String> refreshTokens = new ConcurrentHashMap<>();
        private final Map<String, Boolean> blacklist = new ConcurrentHashMap<>();
        
        @Override
        public User saveUser(User user) {
            log.info("테스트: 사용자 저장 - {}", user.getEmail());
            // 테스트 환경에서는 ID를 직접 설정할 수 없으므로 메모리에 저장할 때 ID를 할당
            if (user.getId() == null) {
                // ID 설정 로직 (실제로는 데이터베이스에서 자동 생성)
                User userWithId = User.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .build();
                
                // ID 직접 설정 - 리플렉션 사용
                try {
                    java.lang.reflect.Field idField = User.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(userWithId, 1L);
                } catch (Exception e) {
                    log.error("사용자 ID 설정 실패", e);
                }
                
                usersByEmail.put(userWithId.getEmail(), userWithId);
                usersById.put(1L, userWithId);
                return userWithId;
            } else {
                usersByEmail.put(user.getEmail(), user);
                usersById.put(user.getId(), user);
                return user;
            }
        }

        @Override
        public Optional<User> findUserByEmail(String email) {
            log.info("테스트: 이메일로 사용자 조회 - {}", email);
            return Optional.ofNullable(usersByEmail.get(email));
        }

        @Override
        public Optional<User> findUserById(Long id) {
            log.info("테스트: ID로 사용자 조회 - {}", id);
            return Optional.ofNullable(usersById.get(id));
        }

        @Override
        public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
            log.info("테스트: 리프레시 토큰 저장 - 사용자 ID: {}, 토큰: {}", userId, refreshToken);
            // 테스트에서는 매번 다른 토큰이 생성되도록 현재 시간을 붙임
            String uniqueToken = refreshToken + "_" + System.currentTimeMillis();
            refreshTokens.put(uniqueToken, userId.toString());
            // 테스트에서는 원래 토큰과 고유 토큰 모두 저장
            refreshTokens.put(refreshToken, userId.toString());
        }

        @Override
        public Optional<String> findUserIdByRefreshToken(String refreshToken) {
            log.info("테스트: 리프레시 토큰으로 사용자 ID 조회 - {}", refreshToken);
            return Optional.ofNullable(refreshTokens.get(refreshToken));
        }

        @Override
        public void deleteRefreshToken(String refreshToken) {
            log.info("테스트: 리프레시 토큰 삭제 - {}", refreshToken);
            refreshTokens.remove(refreshToken);
        }

        @Override
        public void addToBlacklist(String accessToken, long expirationTime) {
            log.info("테스트: 액세스 토큰 블랙리스트 추가 - {}", accessToken);
            blacklist.put(accessToken, true);
        }
        
        // 테스트에서 필요한 추가 기능
        public boolean isTokenBlacklisted(String accessToken) {
            return blacklist.getOrDefault(accessToken, false);
        }
    }
} 