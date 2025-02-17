package com.j30n.stoblyx.adapter.out.persistence;

import com.j30n.stoblyx.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity jpaEntity = new UserJpaEntity();
        jpaEntity.setId(user.getId());
        jpaEntity.setEmail(user.getEmail());
        jpaEntity.setPassword(user.getPassword());
        jpaEntity.setName(user.getName());
        jpaEntity.setRole(user.getRole().name());
        return jpaEntity;
    }

    public User toDomainEntity(UserJpaEntity jpaEntity) {
        return User.builder()
                .id(jpaEntity.getId())
                .email(jpaEntity.getEmail())
                .password(jpaEntity.getPassword())
                .name(jpaEntity.getName())
                .role(User.Role.valueOf(jpaEntity.getRole()))
                .build();
    }
} 