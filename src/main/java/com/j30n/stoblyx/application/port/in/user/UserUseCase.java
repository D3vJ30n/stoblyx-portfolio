package com.j30n.stoblyx.application.port.in.user;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserUseCase {
    /**
     * 현재 사용자의 프로필을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 프로필 정보
     */
    UserProfileResponse getCurrentUser(Long userId);

    /**
     * 사용자 프로필을 수정합니다.
     *
     * @param userId 사용자 ID
     * @param request 수정할 프로필 정보
     * @return 수정된 사용자 프로필 정보
     */
    UserProfileResponse updateUser(Long userId, UserUpdateRequest request);

    /**
     * 사용자 계정을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteUser(Long userId);

    /**
     * 사용자 프로필 이미지를 업데이트합니다.
     * 
     * @param userId 사용자 ID
     * @param image 업로드할 이미지 파일
     * @return 업데이트된 사용자 프로필 정보
     */
    UserProfileResponse updateProfileImage(Long userId, MultipartFile image);
}
