package com.j30n.stoblyx.adapter.out.persistence;

import com.j30n.stoblyx.domain.user.User;
import org.springframework.stereotype.Component;

/**
 * JPA 엔티티와 도메인 엔티티 간의 변환을 담당하는 매퍼
 */
@Component
public class UserMapper {

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환
     *
     * @param user 도메인 엔티티
     * @return JPA 엔티티
     */
    public UserJpaEntity toJpaEntity(User user) {
        if (user == null) {
            return null;
        }

        UserJpaEntity jpaEntity = new UserJpaEntity();
        jpaEntity.setId(user.getId());
        jpaEntity.setEmail(user.getEmail());
        jpaEntity.setPassword(user.getPassword());
        jpaEntity.setName(user.getName());

        // Enum 변환 시 안전하게 처리
        try {
            jpaEntity.setRole(UserJpaEntity.Role.valueOf(user.getRole().name()));
        } catch (IllegalArgumentException e) {
            // 기본값으로 USER 설정
            jpaEntity.setRole(UserJpaEntity.Role.USER);
        }

        return jpaEntity;
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환
     *
     * @param jpaEntity JPA 엔티티
     * @return 도메인 엔티티
     */
    public User toDomainEntity(UserJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return User.builder()
            .id(jpaEntity.getId())
            .email(jpaEntity.getEmail())
            .password(jpaEntity.getPassword())
            .name(jpaEntity.getName())
            .role(mapJpaRoleToDomainRole(jpaEntity.getRole()))
            .build();
    }

    /**
     * JPA Role enum을 도메인 Role enum으로 안전하게 변환
     *
     * @param jpaRole JPA 엔티티의 Role enum
     * @return 도메인 엔티티의 Role enum
     */
    private User.Role mapJpaRoleToDomainRole(UserJpaEntity.Role jpaRole) {
        if (jpaRole == null) {
            return User.Role.USER; // 기본값
        }

        try {
            return User.Role.valueOf(jpaRole.name());
        } catch (IllegalArgumentException e) {
            return User.Role.USER; // 매핑 실패 시 기본값
        }
    }
} 