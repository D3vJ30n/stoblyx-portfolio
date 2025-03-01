package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.application.port.in.user.UserUseCase;
import com.j30n.stoblyx.application.port.in.user.UserInterestUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";

    private final UserUseCase userUseCase;
    private final UserInterestUseCase userInterestUseCase;

    /**
     * 현재 사용자의 프로필을 조회합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자 프로필 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        UserProfileResponse profile = userUseCase.getCurrentUser(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 프로필을 성공적으로 조회했습니다.", profile));
    }

    /**
     * 사용자 프로필을 수정합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param request 수정할 프로필 정보
     * @return 수정된 사용자 프로필 정보
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
        @CurrentUser UserPrincipal userPrincipal,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        UserProfileResponse updatedProfile = userUseCase.updateUser(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 프로필을 성공적으로 수정했습니다.", updatedProfile));
    }

    /**
     * 사용자 계정을 삭제합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        userUseCase.deleteUser(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 계정이 성공적으로 삭제되었습니다.", null));
    }

    /**
     * 사용자의 관심사를 조회합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자 관심사 정보
     */
    @GetMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> getUserInterest(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        UserInterestResponse interestResponse = userInterestUseCase.getUserInterest(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 관심사를 성공적으로 조회했습니다.", interestResponse));
    }

    /**
     * 사용자의 관심사를 업데이트합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param request 수정할 관심사 정보
     * @return 업데이트된 관심사 정보
     */
    @PutMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> updateUserInterest(
        @CurrentUser UserPrincipal userPrincipal,
        @Valid @RequestBody UserInterestRequest request
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        UserInterestResponse updatedInterest = userInterestUseCase.updateUserInterest(userPrincipal.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 관심사를 성공적으로 업데이트했습니다.", updatedInterest));
    }

    /**
     * 사용자 프로필 이미지를 업로드합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param image 업로드할 이미지 파일
     * @return 업데이트된 사용자 프로필 정보
     */
    @PostMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadProfileImage(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam("image") MultipartFile image
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        if (image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 제공되지 않았습니다.");
        }
        
        UserProfileResponse updatedProfile = userUseCase.updateProfileImage(userPrincipal.getId(), image);
        return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "프로필 이미지가 성공적으로 업로드되었습니다.", updatedProfile));
    }
}