package com.j30n.stoblyx.adapter.in.web.dto.admin;

import com.j30n.stoblyx.domain.model.UserRole;
import jakarta.validation.constraints.NotNull;

/**
 * 관리자가 사용자 역할을 변경할 때 사용하는 요청 DTO
 */
public record AdminUserRoleRequest(
    @NotNull(message = "사용자 역할은 필수 항목입니다.")
    UserRole role
) {
    /**
     * 생성자
     *
     * @param role 사용자 역할 (USER, ADMIN)
     */
    public AdminUserRoleRequest {
        // 이미 validation 어노테이션으로 검증되지만, 추가 안전 장치로 null 체크
        if (role == null) {
            throw new IllegalArgumentException("사용자 역할은 null일 수 없습니다.");
        }
    }
} 