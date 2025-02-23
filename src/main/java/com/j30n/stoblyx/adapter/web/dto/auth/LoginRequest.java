package com.j30n.stoblyx.adapter.web.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "사용자 이름은 필수입니다")
    String username,

    @NotBlank(message = "비밀번호는 필수입니다")
    String password
) {
    public LoginRequest {
        if (username != null) username = username.trim();
    }
} 