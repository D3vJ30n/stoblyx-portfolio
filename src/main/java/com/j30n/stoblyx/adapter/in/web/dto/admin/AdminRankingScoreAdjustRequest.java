package com.j30n.stoblyx.adapter.in.web.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 관리자가 사용자 랭킹 점수를 조정할 때 사용하는 요청 DTO
 */
public record AdminRankingScoreAdjustRequest(
    @NotNull(message = "점수 조정값은 필수 항목입니다.")
    Integer scoreAdjustment,
    
    @NotBlank(message = "조정 사유는 필수 항목입니다.")
    String reason
) {
    /**
     * 생성자
     *
     * @param scoreAdjustment 점수 조정값 (양수 또는 음수)
     * @param reason 조정 사유
     */
    public AdminRankingScoreAdjustRequest {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("조정 사유는 비어있을 수 없습니다.");
        }
    }
} 