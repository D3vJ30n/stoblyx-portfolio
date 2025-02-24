package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;

public interface UserService {
    UserProfileResponse getCurrentUser(Long userId);
    UserProfileResponse updateUser(Long userId, UserUpdateRequest request);
    void deleteUser(Long userId);
} 