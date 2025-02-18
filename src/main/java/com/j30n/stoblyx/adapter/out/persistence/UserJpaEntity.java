package com.j30n.stoblyx.adapter.out.persistence;

import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * 사용자 정보를 저장하는 JPA 엔티티
 * users 테이블과 매핑됩니다.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 고유 식별자")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("사용자 이메일 (로그인 ID로 사용)")
    private String email;

    @Column(nullable = false, length = 100)
    @Comment("암호화된 비밀번호")
    private String password;

    @Column(nullable = false, length = 50)
    @Comment("사용자 이름")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("사용자 권한 (USER, ADMIN)")
    private Role role;

    /**
     * 도메인 엔티티로부터 JPA 엔티티 생성
     */
    public static UserJpaEntity fromDomainEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setRole(Role.valueOf(user.getRole().name()));
        return entity;
    }

    /**
     * 도메인 엔티티로 변환
     */
    public User toDomainEntity() {
        return User.builder()
            .id(this.id)
            .email(this.email)
            .password(this.password)
            .name(this.name)
            .role(User.Role.valueOf(this.role.name()))
            .build();
    }

    /**
     * 사용자 권한을 정의하는 열거형
     */
    public enum Role {
        USER,   // 일반 사용자
        ADMIN   // 관리자
    }
} 