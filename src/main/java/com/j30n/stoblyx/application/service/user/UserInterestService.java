package com.j30n.stoblyx.application.service.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;

public interface UserInterestService {
    UserInterestResponse getUserInterest(Long userId);
    UserInterestResponse updateUserInterest(Long userId, UserInterestRequest request);
} 