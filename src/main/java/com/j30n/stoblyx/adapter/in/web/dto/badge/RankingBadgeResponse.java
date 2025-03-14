package com.j30n.stoblyx.adapter.in.web.dto.badge;

import java.time.LocalDateTime;

/**
 * 배지 정보를 응답하기 위한 DTO
 */
public record RankingBadgeResponse(
    Long id,
    String badgeType,
    String name,
    String description,
    String imageUrl,
    String requirementType,
    Integer thresholdValue,
    LocalDateTime achievedAt,
    Boolean isAchieved
) {
    /**
     * 배지 DTO의 컴팩트 생성자
     */
    public RankingBadgeResponse {
        // ID가 null이면 예외 발생
        if (id == null) {
            throw new IllegalArgumentException("배지 ID는 null일 수 없습니다.");
        }

        // badgeType이 null이거나 빈 문자열이면 예외 발생
        if (badgeType == null || badgeType.isBlank()) {
            throw new IllegalArgumentException("배지 유형은 null이거나 빈 문자열일 수 없습니다.");
        }

        // name이 null이거나 빈 문자열이면 예외 발생
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("배지 이름은 null이거나 빈 문자열일 수 없습니다.");
        }
    }
} 