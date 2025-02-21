package com.j30n.stoblyx.adapter.out.persistence.entity;

import com.j30n.stoblyx.domain.base.BaseTimeEntity;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * 사용자 정보를 저장하는 JPA 엔티티 클래스입니다.
 * users 테이블과 매핑되며, 사용자의 기본 정보와 인증 관련 정보를 관리합니다.
 *
 * @see User 도메인 모델
 * @see Role 사용자 권한
 * @see BaseTimeEntity 기본 시간 정보
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
public class UserJpaEntity extends BaseTimeEntity {

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
     * 도메인 엔티티로부터 JPA 엔티티를 생성합니다.
     *
     * @param user 변환할 도메인 엔티티
     * @return 생성된 JPA 엔티티
     * @throws IllegalArgumentException user가 null인 경우
     */
    public static UserJpaEntity fromDomainEntity(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User domain entity cannot be null");
        }

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setRole(Role.valueOf(user.getRole().name()));
        return entity;
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     *
     * @return 변환된 도메인 엔티티
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