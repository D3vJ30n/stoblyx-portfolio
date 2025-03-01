package com.j30n.stoblyx.application.port.in.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;

public interface UserInterestUseCase {
    /**
     * 사용자의 관심사 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 관심사 정보
     */
    UserInterestResponse getUserInterest(Long userId);

    /**
     * 사용자의 관심사 정보를 수정합니다.
     *
     * @param userId 사용자 ID
     * @param request 수정할 관심사 정보
     * @return 수정된 관심사 정보
     */
    UserInterestResponse updateUserInterest(Long userId, UserInterestRequest request);
}
