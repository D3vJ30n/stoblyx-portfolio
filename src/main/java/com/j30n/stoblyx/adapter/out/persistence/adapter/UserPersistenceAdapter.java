package com.j30n.stoblyx.adapter.out.persistence.adapter;

import com.j30n.stoblyx.adapter.out.persistence.entity.UserJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.mapper.UserMapper;
import com.j30n.stoblyx.adapter.out.persistence.repository.UserRepository;
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
     * @throws IllegalArgumentException        사용자 정보가 null이거나 필수 필드가 누락된 경우
     * @throws DataIntegrityViolationException 이메일 중복 등 데이터 무결성 위반 시
     */
    @Override
    @Transactional
    public User save(User user) {
        if (user == null) {
            log.error("사용자 정보가 null입니다");
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다");
        }

        try {
            log.debug("사용자 저장 시도 - 이메일: {}", user.getEmail());
            UserJpaEntity userJpaEntity = userMapper.toJpaEntity(user);

            if (userJpaEntity == null) {
                log.error("사용자 변환 중 오류가 발생했습니다");
                throw new IllegalArgumentException("사용자 변환 중 오류가 발생했습니다");
            }

            UserJpaEntity savedEntity = userRepository.save(userJpaEntity);
            User savedUser = userMapper.toDomainEntity(savedEntity);
            log.info("사용자 저장 완료 - ID: {}", savedUser.getId());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            log.error("사용자 저장 실패 - 이메일: {}, 오류: {}", user.getEmail(), e.getMessage());
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
            log.error("ID가 null인 사용자는 조회할 수 없습니다");
            return Optional.empty();
        }

        log.debug("사용자 조회 시도 - ID: {}", id);
        return userRepository.findById(id)
            .map(entity -> {
                User user = userMapper.toDomainEntity(entity);
                log.debug("사용자 조회 완료 - 이메일: {}", user.getEmail());
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
            log.error("이메일이 null이거나 비어있어 조회할 수 없습니다");
            return Optional.empty();
        }

        log.debug("사용자 조회 시도 - 이메일: {}", email);
        return userRepository.findByEmail(email)
            .map(entity -> {
                User user = userMapper.toDomainEntity(entity);
                log.debug("사용자 조회 완료 - ID: {}", user.getId());
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
            log.error("이메일이 null이거나 비어있어 확인할 수 없습니다");
            return false;
        }

        log.debug("이메일 존재 여부 확인 - 이메일: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.debug("이메일 {} 존재 여부: {}", email, exists);
        return exists;
    }
}