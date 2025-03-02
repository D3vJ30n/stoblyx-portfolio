package com.j30n.stoblyx.application.service.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserRoleRequest;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminUserStatusRequest;
import com.j30n.stoblyx.application.port.in.admin.AdminUserUseCase;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자의 사용자 관리 기능을 구현한 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService implements AdminUserUseCase {

    private static final String ERROR_USER_NOT_FOUND = "존재하지 않는 사용자입니다.";
    
    private final UserPort userPort;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        log.debug("관리자: 모든 사용자 목록 조회, pageable={}", pageable);
        return userPort.findAll(pageable)
            .map(AdminUserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(Long userId) {
        log.debug("관리자: 사용자 상세 조회, userId={}", userId);
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
        return AdminUserResponse.from(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserStatus(Long userId, AdminUserStatusRequest request) {
        log.debug("관리자: 사용자 상태 변경, userId={}, status={}", userId, request.accountStatus());
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
        
        user.updateStatus(request.accountStatus());
        userPort.save(user);
        
        return AdminUserResponse.from(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserRole(Long userId, AdminUserRoleRequest request) {
        log.debug("관리자: 사용자 역할 변경, userId={}, role={}", userId, request.role());
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
        
        user.updateRole(request.role());
        userPort.save(user);
        
        return AdminUserResponse.from(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("관리자: 사용자 삭제, userId={}", userId);
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
        
        user.delete();
        userPort.save(user);
    }
} 