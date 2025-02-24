package com.j30n.stoblyx.adapter.in.web.dto.user;

import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.UserRole;

public record UserProfileResponse(
    Long id,
    String username,
    String nickname,
    String email,
    UserRole role
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getRole()
        );
    }
} 