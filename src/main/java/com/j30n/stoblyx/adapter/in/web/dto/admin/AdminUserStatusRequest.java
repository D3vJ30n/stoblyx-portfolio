package com.j30n.stoblyx.adapter.in.web.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 관리자가 사용자 상태를 변경할 때 사용하는 요청 DTO
 */
public record AdminUserStatusRequest(
    @NotBlank(message = "계정 상태는 필수 항목입니다.")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED|BANNED)$", message = "계정 상태는 ACTIVE, INACTIVE, SUSPENDED, BANNED 중 하나여야 합니다.")
    String accountStatus
) {
    /**
     * 생성자
     *
     * @param accountStatus 계정 상태 (ACTIVE, INACTIVE, SUSPENDED, BANNED)
     */
    public AdminUserStatusRequest {
        // 이미 validation 어노테이션으로 검증되지만, 추가 안전 장치로 null 체크
        if (accountStatus == null) {
            throw new IllegalArgumentException("계정 상태는 null일 수 없습니다.");
        }
    }
} 