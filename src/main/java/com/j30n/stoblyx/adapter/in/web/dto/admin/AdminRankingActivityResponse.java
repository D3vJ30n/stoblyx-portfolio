package com.j30n.stoblyx.adapter.in.web.dto.admin;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;

import java.time.LocalDateTime;

/**
 * 관리자가 사용자 활동 내역을 조회할 때 사용되는 응답 DTO
 */
public record AdminRankingActivityResponse(
    Long id,
    Long userId,
    ActivityType activityType,
    Integer scoreChange,
    String ipAddress,
    Long targetId,
    String targetType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * RankingUserActivity 엔티티로부터 AdminRankingActivityResponse를 생성합니다.
     *
     * @param activity 랭킹 사용자 활동 엔티티
     * @return AdminRankingActivityResponse 객체
     */
    public static AdminRankingActivityResponse from(RankingUserActivity activity) {
        return new AdminRankingActivityResponse(
            activity.getId(),
            activity.getUserId(),
            activity.getActivityType(),
            activity.getPoints(),
            "",
            activity.getReferenceId(),
            activity.getReferenceType(),
            activity.getCreatedAt(),
            activity.getModifiedAt()
        );
    }
} 