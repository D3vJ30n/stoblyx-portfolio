package com.j30n.stoblyx.adapter.out.persistence.mapper;

import com.j30n.stoblyx.adapter.out.persistence.entity.UserJpaEntity;
import com.j30n.stoblyx.domain.model.user.User;
import org.springframework.stereotype.Component;

/**
 * User 도메인 모델과 JPA 엔티티 간의 양방향 변환을 담당하는 매퍼
 */
@Component
public class UserMapper {

    /**
     * 도메인 엔티티를 JPA 엔티티로 변환합니다.
     * null이나 잘못된 Role 값이 입력될 경우 안전하게 처리합니다.
     *
     * @param user 변환할 도메인 엔티티
     * @return 변환된 JPA 엔티티, user가 null인 경우 null 반환
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
        jpaEntity.setRole(mapDomainRoleToJpaRole(user.getRole()));

        return jpaEntity;
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @param jpaEntity 변환할 JPA 엔티티
     * @return 변환된 도메인 엔티티, jpaEntity가 null인 경우 null 반환
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
     * 도메인 Role enum을 JPA Role enum으로 안전하게 변환합니다.
     */
    private UserJpaEntity.Role mapDomainRoleToJpaRole(User.Role domainRole) {
        if (domainRole == null) {
            return UserJpaEntity.Role.USER;  // 기본값
        }

        try {
            return UserJpaEntity.Role.valueOf(domainRole.name());
        } catch (IllegalArgumentException e) {
            return UserJpaEntity.Role.USER;  // 매핑 실패 시 기본값
        }
    }

    /**
     * JPA Role enum을 도메인 Role enum으로 안전하게 변환합니다.
     */
    private User.Role mapJpaRoleToDomainRole(UserJpaEntity.Role jpaRole) {
        if (jpaRole == null) {
            return User.Role.USER;  // 기본값
        }

        try {
            return User.Role.valueOf(jpaRole.name());
        } catch (IllegalArgumentException e) {
            return User.Role.USER;  // 매핑 실패 시 기본값
        }
    }
} 