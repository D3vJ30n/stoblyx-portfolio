package com.j30n.stoblyx.adapter.out.persistence.auth;

import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 인증 관련 영속성 어댑터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString(), expirationTime, TimeUnit.SECONDS);
    }

    @Override
    public Optional<String> findUserIdByRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(userId);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    @Override
    public void addToBlacklist(String accessToken, long expirationTime) {
        String key = BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "true", expirationTime, TimeUnit.SECONDS);
    }
}
