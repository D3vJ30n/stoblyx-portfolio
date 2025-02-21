package com.j30n.stoblyx.application.usecase.user.port;

import com.j30n.stoblyx.domain.model.user.User;

/**
 * 사용자 조회를 위한 유스케이스
 */
public interface FindUserUseCase {
    UserResponse findById(Long id);

    UserResponse findByEmail(String email);

    record UserResponse(
        Long id,
        String email,
        String name,
        String password,
        User.Role role
    ) {
    }
} 