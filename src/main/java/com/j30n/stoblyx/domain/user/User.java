package com.j30n.stoblyx.domain.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 사용자 도메인 엔티티
 * 불변 객체로 구현되어 있으며, 모든 필드는 final로 선언됩니다.
 */
@Getter
@ToString(exclude = "password") // 보안을 위해 password 필드는 toString에서 제외
@EqualsAndHashCode
public class User {
    @NotNull(message = "ID는 null일 수 없습니다")
    private final Long id;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    private final String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
    private final String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private final String name;

    @NotNull(message = "역할은 필수입니다")
    private final Role role;

    @Builder
    public User(
            @NotNull Long id,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String name,
            @NotNull Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    /**
     * 사용자 역할을 정의하는 열거형
     */
    public enum Role {
        USER,    // 일반 사용자
        ADMIN    // 관리자
    }
} 