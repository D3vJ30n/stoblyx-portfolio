package com.j30n.stoblyx.adapter.in.web.dto.reward;

import java.time.LocalDateTime;

/**
 * 보상 정보를 응답하기 위한 DTO
 */
public record GamificationRewardResponse(
    Long id,
    String rewardType,
    String title,
    String description,
    Integer pointValue,
    Boolean isClaimed,
    LocalDateTime claimedAt,
    LocalDateTime expiresAt,
    String benefitCode,
    String imageUrl
) {
    /**
     * 보상 DTO의 컴팩트 생성자
     */
    public GamificationRewardResponse {
        // ID가 null이면 예외 발생
        if (id == null) {
            throw new IllegalArgumentException("보상 ID는 null일 수 없습니다.");
        }

        // rewardType이 null이거나 빈 문자열이면 예외 발생
        if (rewardType == null || rewardType.isBlank()) {
            throw new IllegalArgumentException("보상 유형은 null이거나 빈 문자열일 수 없습니다.");
        }

        // title이 null이거나 빈 문자열이면 예외 발생
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("보상 제목은 null이거나 빈 문자열일 수 없습니다.");
        }
    }
} 