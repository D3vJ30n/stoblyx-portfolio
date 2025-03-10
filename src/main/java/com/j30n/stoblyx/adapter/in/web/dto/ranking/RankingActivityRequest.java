package com.j30n.stoblyx.adapter.in.web.dto.ranking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 활동 점수 업데이트 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingActivityRequest {
    /**
     * 활동 유형 (예: CONTENT_CREATE, COMMENT_CREATE, LIKE, SHARE 등)
     */
    @NotBlank(message = "활동 유형은 필수 입력값입니다.")
    private String activityType;
    
    /**
     * 활동 점수
     */
    @NotNull(message = "활동 점수는 필수 입력값입니다.")
    @Min(value = 0, message = "활동 점수는 0 이상이어야 합니다.")
    private Integer score;
} 