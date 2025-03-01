package com.j30n.stoblyx.adapter.out.persistence.auth;

import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.domain.model.Auth;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.AuthRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final AuthRepository authRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    @Transactional
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
    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString(), expirationTime, TimeUnit.SECONDS);
        
        // Auth 엔티티에도 저장
        userRepository.findById(userId).ifPresent(user -> {
            LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(expirationTime);
            
            // 기존 인증 정보가 있는지 확인
            Optional<Auth> existingAuth = authRepository.findByUser(user);
            
            if (existingAuth.isPresent()) {
                // 기존 인증 정보 업데이트
                Auth auth = existingAuth.get();
                auth.updateRefreshToken(refreshToken, expiryDate);
                authRepository.save(auth);
            } else {
                // 새 인증 정보 생성
                Auth auth = Auth.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .expiryDate(expiryDate)
                        .build();
                authRepository.save(auth);
            }
        });
    }

    @Override
    public Optional<String> findUserIdByRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(key);
        
        if (userId == null) {
            // Redis에 없으면 DB에서 확인
            Optional<Auth> auth = authRepository.findByRefreshToken(refreshToken);
            if (auth.isPresent() && !auth.get().isExpired()) {
                auth.get().updateLastUsedAt();
                authRepository.save(auth.get());
                return Optional.of(auth.get().getUser().getId().toString());
            }
        }
        
        return Optional.ofNullable(userId);
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
        
        // DB에서도 제거
        authRepository.findByRefreshToken(refreshToken).ifPresent(auth -> {
            auth.updateRefreshToken(null, LocalDateTime.now());
            authRepository.save(auth);
        });
    }

    @Override
    public void addToBlacklist(String accessToken, long expirationTime) {
        String key = BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "true", expirationTime, TimeUnit.SECONDS);
    }
}
