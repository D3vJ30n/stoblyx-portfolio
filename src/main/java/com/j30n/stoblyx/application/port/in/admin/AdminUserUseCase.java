package com.j30n.stoblyx.application.port.in.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserRoleRequest;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 관리자의 사용자 관리 기능을 위한 유스케이스 인터페이스
 */
public interface AdminUserUseCase {

    /**
     * 모든 사용자 목록을 페이징하여 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    Page<AdminUserResponse> getAllUsers(Pageable pageable);

    /**
     * 특정 사용자를 ID로 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    AdminUserResponse getUserById(Long userId);

    /**
     * 사용자 계정 상태를 변경합니다.
     *
     * @param userId 사용자 ID
     * @param request 상태 변경 요청 정보
     * @return 변경된 사용자 정보
     */
    AdminUserResponse updateUserStatus(Long userId, AdminUserStatusRequest request);

    /**
     * 사용자 역할을 변경합니다.
     *
     * @param userId 사용자 ID
     * @param request 역할 변경 요청 정보
     * @return 변경된 사용자 정보
     */
    AdminUserResponse updateUserRole(Long userId, AdminUserRoleRequest request);
    
    /**
     * 사용자를 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteUser(Long userId);
} 