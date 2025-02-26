package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.application.port.in.user.UserUseCase;
import com.j30n.stoblyx.application.port.in.user.UserInterestUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserUseCase userUseCase;
    private final UserInterestUseCase userInterestUseCase;

    /**
     * 현재 사용자의 프로필을 조회합니다.
     *
     * @param userId 현재 사용자 ID
     * @return 사용자 프로필 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
        @CurrentUser Long userId
    ) {
        try {
            UserProfileResponse profile = userUseCase.getCurrentUser(userId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 프로필을 성공적으로 조회했습니다.", profile));
        } catch (Exception e) {
            log.error("사용자 프로필 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 프로필 조회에 실패했습니다.", null));
        }
    }

    /**
     * 사용자 프로필을 수정합니다.
     *
     * @param userId 현재 사용자 ID
     * @param request 수정할 프로필 정보
     * @return 수정된 사용자 프로필 정보
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
        @CurrentUser Long userId,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        try {
            UserProfileResponse updatedProfile = userUseCase.updateUser(userId, request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 프로필을 성공적으로 수정했습니다.", updatedProfile));
        } catch (Exception e) {
            log.error("사용자 프로필 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 프로필 수정에 실패했습니다.", null));
        }
    }

    /**
     * 사용자 계정을 삭제합니다.
     *
     * @param userId 현재 사용자 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @CurrentUser Long userId
    ) {
        try {
            userUseCase.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 계정을 성공적으로 삭제했습니다.", null));
        } catch (Exception e) {
            log.error("사용자 계정 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 계정 삭제에 실패했습니다.", null));
        }
    }

    /**
     * 사용자의 관심사 정보를 조회합니다.
     *
     * @param userId 현재 사용자 ID
     * @return 사용자의 관심사 정보
     */
    @GetMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> getUserInterest(
        @CurrentUser Long userId
    ) {
        try {
            UserInterestResponse interests = userInterestUseCase.getUserInterest(userId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 관심사를 성공적으로 조회했습니다.", interests));
        } catch (Exception e) {
            log.error("사용자 관심사 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 관심사 조회에 실패했습니다.", null));
        }
    }

    /**
     * 사용자의 관심사 정보를 수정합니다.
     *
     * @param userId 현재 사용자 ID
     * @param request 수정할 관심사 정보
     * @return 수정된 관심사 정보
     */
    @PutMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> updateUserInterest(
        @CurrentUser Long userId,
        @Valid @RequestBody UserInterestRequest request
    ) {
        try {
            UserInterestResponse updatedInterests = userInterestUseCase.updateUserInterest(userId, request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 관심사를 성공적으로 수정했습니다.", updatedInterests));
        } catch (Exception e) {
            log.error("사용자 관심사 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 관심사 수정에 실패했습니다.", null));
        }
    }
}