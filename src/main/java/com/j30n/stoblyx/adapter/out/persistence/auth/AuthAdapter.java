package com.j30n.stoblyx.adapter.out.persistence.auth;

import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.domain.model.Auth;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.AuthRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
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
        try {
            log.info("사용자 저장: {}", user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("사용자 저장 실패: {}, 원인: {}", user.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try {
            log.debug("이메일로 사용자 조회: {}", email);
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            log.error("이메일로 사용자 조회 실패: {}, 원인: {}", email, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserById(Long id) {
        try {
            log.debug("ID로 사용자 조회: {}", id);
            return userRepository.findById(id);
        } catch (Exception e) {
            log.error("ID로 사용자 조회 실패: {}, 원인: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        log.info("리프레시 토큰 저장: userId={}", userId);
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        
        // 1. Redis에 저장 시도
        try {
            redisTemplate.opsForValue().set(key, userId.toString(), expirationTime, TimeUnit.SECONDS);
            log.debug("Redis에 리프레시 토큰 저장 성공: userId={}", userId);
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패로 인해 리프레시 토큰을 DB에만 저장합니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Redis에 리프레시 토큰 저장 실패: userId={}, 원인: {}", userId, e.getMessage());
        }
        
        // 2. DB에 저장 (항상 수행)
        try {
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
                    log.debug("기존 Auth 엔티티 업데이트 성공: userId={}", userId);
                } else {
                    // 새 인증 정보 생성
                    Auth auth = Auth.builder()
                            .user(user)
                            .refreshToken(refreshToken)
                            .expiryDate(expiryDate)
                            .tokenType("Bearer")
                            .build();
                    authRepository.save(auth);
                    log.debug("새 Auth 엔티티 생성 성공: userId={}", userId);
                }
            });
        } catch (Exception e) {
            log.error("DB에 리프레시 토큰 저장 실패: userId={}, 원인: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<String> findUserIdByRefreshToken(String refreshToken) {
        log.info("리프레시 토큰으로 사용자 ID 조회: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...");
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = null;
        
        // 1. Redis에서 먼저 조회
        try {
            userId = redisTemplate.opsForValue().get(key);
            if (userId != null) {
                log.debug("Redis에서 리프레시 토큰으로 사용자 ID 조회 성공: {}", userId);
                return Optional.of(userId);
            }
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패로 인해 DB에서만 리프레시 토큰을 조회합니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Redis에서 리프레시 토큰 조회 실패: {}, 원인: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...", e.getMessage());
        }
        
        // 2. Redis에 없거나 조회 실패 시 DB에서 확인
        try {
            Optional<Auth> auth = authRepository.findByRefreshToken(refreshToken);
            if (auth.isPresent() && !auth.get().isExpired()) {
                auth.get().updateLastUsedAt();
                authRepository.save(auth.get());
                log.debug("DB에서 리프레시 토큰으로 사용자 ID 조회 성공: {}", auth.get().getUser().getId());
                return Optional.of(auth.get().getUser().getId().toString());
            }
        } catch (Exception e) {
            log.error("DB에서 리프레시 토큰 조회 실패: {}, 원인: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...", e.getMessage(), e);
        }
        
        // 3. 어디에서도 찾지 못한 경우
        return Optional.ofNullable(userId);
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        log.info("리프레시 토큰 삭제: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...");
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        
        // 1. Redis에서 삭제 시도
        try {
            redisTemplate.delete(key);
            log.debug("Redis에서 리프레시 토큰 삭제 성공");
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패로 인해 DB에서만 리프레시 토큰을 삭제합니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Redis에서 리프레시 토큰 삭제 실패: {}, 원인: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...", e.getMessage());
        }
        
        // 2. DB에서도 제거 (항상 수행)
        try {
            authRepository.findByRefreshToken(refreshToken).ifPresent(auth -> {
                auth.updateRefreshToken(null, LocalDateTime.now());
                authRepository.save(auth);
                log.debug("DB에서 리프레시 토큰 삭제 성공");
            });
        } catch (Exception e) {
            log.error("DB에서 리프레시 토큰 삭제 실패: {}, 원인: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...", e.getMessage(), e);
        }
    }

    @Override
    public void addToBlacklist(String accessToken, long expirationTime) {
        log.info("액세스 토큰 블랙리스트 추가: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...");
        String key = BLACKLIST_PREFIX + accessToken;
        
        // Redis에 블랙리스트 토큰 추가 시도
        try {
            redisTemplate.opsForValue().set(key, "true", expirationTime, TimeUnit.SECONDS);
            log.debug("액세스 토큰 블랙리스트 추가 성공");
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패로 인해 액세스 토큰 블랙리스트 추가를 건너뜁니다: {}", e.getMessage());
        } catch (Exception e) {
            log.error("액세스 토큰 블랙리스트 추가 실패: {}, 원인: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...", e.getMessage());
        }
    }
    
    /**
     * 토큰이 블랙리스트에 있는지 확인
     * 
     * @param accessToken 확인할 액세스 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isTokenBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        
        try {
            Boolean result = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패로 인해 블랙리스트 확인을 건너뜁니다. 토큰 유효성을 허용합니다.: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("블랙리스트 확인 중 오류 발생: {}, 원인: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...", e.getMessage());
            return false;
        }
    }
}
