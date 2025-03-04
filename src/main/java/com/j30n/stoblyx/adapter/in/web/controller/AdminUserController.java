package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserRoleRequest;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserStatusRequest;
import com.j30n.stoblyx.application.port.in.admin.AdminUserUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자의 사용자 관리 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserUseCase adminUserUseCase;

    /**
     * 모든 사용자 목록을 페이징하여 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllUsers(
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        try {
            Page<AdminUserResponse> users = adminUserUseCase.getAllUsers(pageable);
            return ResponseEntity.ok(ApiResponse.success("사용자 목록 조회에 성공했습니다.", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 특정 사용자를 ID로 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(@PathVariable Long userId) {
        try {
            AdminUserResponse user = adminUserUseCase.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자 조회에 성공했습니다.", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자 계정 상태를 변경합니다.
     *
     * @param userId 사용자 ID
     * @param request 상태 변경 요청 정보
     * @return 변경된 사용자 정보
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserStatus(
        @PathVariable Long userId,
        @Valid @RequestBody AdminUserStatusRequest request
    ) {
        try {
            AdminUserResponse user = adminUserUseCase.updateUserStatus(userId, request);
            return ResponseEntity.ok(ApiResponse.success("사용자 상태가 성공적으로 변경되었습니다.", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자 역할을 변경합니다.
     *
     * @param userId 사용자 ID
     * @param request 역할 변경 요청 정보
     * @return 변경된 사용자 정보
     */
    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserRole(
        @PathVariable Long userId,
        @Valid @RequestBody AdminUserRoleRequest request
    ) {
        try {
            AdminUserResponse user = adminUserUseCase.updateUserRole(userId, request);
            return ResponseEntity.ok(ApiResponse.success("사용자 역할이 성공적으로 변경되었습니다.", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자를 삭제합니다.
     *
     * @param userId 사용자 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        try {
            adminUserUseCase.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
} 