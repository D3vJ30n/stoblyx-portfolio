package com.j30n.stoblyx.adapter.in.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 * username 또는 email을 사용하여 로그인 가능
 */
public record LoginRequest(
    String username,
    
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    String password
) {
    /**
     * 기본 생성자 - 모든 필드 초기화
     */
    public LoginRequest {
        if (username != null) username = username.trim();
        if (email != null) email = email.trim();
        
        // 로그인 식별자(username 또는 email) 중 하나는 반드시 있어야 함
        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
            throw new IllegalArgumentException("사용자 이름 또는 이메일은 필수입니다");
        }
    }
    
    /**
     * 사용자 이름과 비밀번호만으로 인스턴스 생성하는 보조 생성자
     * @param username 사용자 이름
     * @param password 비밀번호
     */
    public LoginRequest(String username, String password) {
        this(username, null, password);
    }
    
    /**
     * 로그인 식별자(username 또는 email) 반환
     * @return 로그인에 사용할 식별자
     */
    @JsonIgnore
    public String getLoginIdentifier() {
        return (email != null && !email.isEmpty()) ? email : username;
    }
} 