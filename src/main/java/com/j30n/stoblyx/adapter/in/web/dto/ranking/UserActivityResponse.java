package com.j30n.stoblyx.adapter.in.web.dto.ranking;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;

import java.time.LocalDateTime;

/**
 * 사용자 활동 내역 응답 DTO
 */
public record UserActivityResponse(
    Long id,
    Long userId,
    ActivityType activityType,
    int points,
    LocalDateTime activityDate,
    Long referenceId,
    String referenceType
) {
    /**
     * RankingUserActivity 엔티티로부터 UserActivityResponse DTO를 생성합니다.
     *
     * @param activity 사용자 활동 내역 엔티티
     * @return 사용자 활동 내역 응답 DTO
     */
    public static UserActivityResponse fromEntity(RankingUserActivity activity) {
        return new UserActivityResponse(
            activity.getId(),
            activity.getUserId(),
            activity.getActivityType(),
            activity.getPoints(),
            activity.getCreatedAt(),
            activity.getReferenceId(),
            activity.getReferenceType()
        );
    }
} 