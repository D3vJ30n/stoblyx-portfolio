package com.j30n.stoblyx.adapter.in.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotEmpty(message = "닉네임은 필수입니다")
    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    String nickname,

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    String email,
    
    String profileImageUrl
) {
    public UserUpdateRequest {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다");
        }
    }
} 