package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.application.service.user.UserService;
import com.j30n.stoblyx.application.service.user.UserInterestService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * 사용자 프로필 조회, 수정, 삭제 기능을 제공합니다.
 * 모든 엔드포인트는 API 버전 v1을 사용합니다.
 * 인증된 사용자만 접근이 가능합니다.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserInterestService userInterestService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
        @CurrentUser UserPrincipal currentUser
    ) {
        try {
            UserProfileResponse profile = userService.getCurrentUser(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 프로필을 성공적으로 조회했습니다.", profile));
        } catch (Exception e) {
            log.error("사용자 프로필 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 프로필 조회에 실패했습니다.", null));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        try {
            UserProfileResponse updatedProfile = userService.updateUser(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 프로필을 성공적으로 수정했습니다.", updatedProfile));
        } catch (Exception e) {
            log.error("사용자 프로필 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 프로필 수정에 실패했습니다.", null));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @CurrentUser UserPrincipal currentUser
    ) {
        try {
            userService.deleteUser(currentUser.getId());
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
     * @param currentUser 현재 인증된 사용자
     * @return 사용자의 관심사 정보
     */
    @GetMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> getUserInterest(
        @CurrentUser UserPrincipal currentUser
    ) {
        try {
            UserInterestResponse interests = userInterestService.getUserInterest(currentUser.getId());
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
     * @param currentUser 현재 인증된 사용자
     * @param request 수정할 관심사 정보
     * @return 수정된 관심사 정보
     */
    @PutMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> updateUserInterest(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody UserInterestRequest request
    ) {
        try {
            UserInterestResponse updatedInterests = userInterestService.updateUserInterest(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 관심사를 성공적으로 수정했습니다.", updatedInterests));
        } catch (Exception e) {
            log.error("사용자 관심사 수정 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", "사용자 관심사 수정에 실패했습니다.", null));
        }
    }
} 