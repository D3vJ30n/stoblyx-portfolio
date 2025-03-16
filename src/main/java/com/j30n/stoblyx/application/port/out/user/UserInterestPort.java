package com.j30n.stoblyx.application.port.out.user;

import com.j30n.stoblyx.domain.model.UserInterest;

import java.util.Optional;

public interface UserInterestPort {
    /**
     * 사용자의 관심사 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 관심사 정보 Optional
     */
    Optional<UserInterest> findByUserId(Long userId);

    /**
     * 관심사 정보를 저장합니다.
     *
     * @param userInterest 저장할 관심사 정보
     * @return 저장된 관심사 정보
     */
    UserInterest save(UserInterest userInterest);
}
