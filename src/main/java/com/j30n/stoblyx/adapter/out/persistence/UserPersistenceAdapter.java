package com.j30n.stoblyx.adapter.out.persistence;

import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.port.out.UserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 영속성 어댑터
 * 도메인 엔티티와 JPA 엔티티 간의 변환을 처리하고 데이터베이스 작업을 수행합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPersistenceAdapter implements UserPort {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 사용자 정보를 저장합니다.
     *
     * @param user 저장할 사용자 정보
     * @return 저장된 사용자 정보
     * @throws IllegalArgumentException        사용자 정보가 null인 경우
     * @throws DataIntegrityViolationException 이메일 중복 등 데이터 무결성 위반 시
     */
    @Override
    @Transactional
    public User save(User user) {
        if (user == null) {
            log.error("Cannot save null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            log.debug("Saving user with email: {}", user.getEmail());
            UserJpaEntity savedEntity = userRepository.save(userMapper.toJpaEntity(user));
            User savedUser = userMapper.toDomainEntity(savedEntity);
            log.info("Successfully saved user with ID: {}", savedUser.getId());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to save user with email: {}. Error: {}", user.getEmail(), e.getMessage());
            throw e;
        }
    }

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 조회된 사용자 정보 (Optional)
     */
    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            log.error("Cannot find user with null ID");
            return Optional.empty();
        }

        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
            .map(entity -> {
                User user = userMapper.toDomainEntity(entity);
                log.debug("Found user: {}", user.getEmail());
                return user;
            });
    }

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일
     * @return 조회된 사용자 정보 (Optional)
     */
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.error("Cannot find user with null or empty email");
            return Optional.empty();
        }

        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
            .map(entity -> {
                User user = userMapper.toDomainEntity(entity);
                log.debug("Found user with ID: {}", user.getId());
                return user;
            });
    }

    /**
     * 이메일 존재 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 이메일 존재 여부
     */
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.error("Cannot check existence with null or empty email");
            return false;
        }

        log.debug("Checking email existence: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.debug("Email {} exists: {}", email, exists);
        return exists;
    }
} 