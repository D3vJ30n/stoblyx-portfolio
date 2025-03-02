package com.j30n.stoblyx.adapter.in.web.dto.admin;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;

import java.time.LocalDateTime;

/**
 * 관리자가 사용자 정보를 조회할 때 사용되는 응답 DTO
 */
public record AdminUserResponse(
    Long id,
    String username,
    String nickname,
    String email,
    String profileImageUrl,
    UserRole role,
    String accountStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {
    /**
     * User 엔티티로부터 AdminUserResponse를 생성합니다.
     *
     * @param user 사용자 엔티티
     * @return AdminUserResponse 객체
     */
    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getProfileImageUrl(),
            user.getRole(),
            user.getAccountStatus(),
            user.getCreatedAt(),
            user.getModifiedAt(),
            user.getLastLoginAt()
        );
    }
    
    public static AdminUserResponse errorResponse() {
        return new AdminUserResponse(
            null, "Error", "Error", "error@example.com", null, 
            UserRole.USER, "INACTIVE", null, null, null
        );
    }
} 