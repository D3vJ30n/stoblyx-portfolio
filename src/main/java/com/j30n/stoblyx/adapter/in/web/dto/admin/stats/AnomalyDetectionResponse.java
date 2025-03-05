package com.j30n.stoblyx.adapter.in.web.dto.admin.stats;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이상 활동 탐지 응답 객체
 */
@Getter
@Builder
public class AnomalyDetectionResponse {

    // 이상 활동 정보
    private final Long userId;
    private final String username;
    private final String anomalyType;
    private final String description;
    private final LocalDateTime detectedAt;
    private final double severityScore;
    private final boolean isResolved;

    // 이상 활동 상세 정보
    private final long scoreBeforeAnomaly;
    private final long scoreAfterAnomaly;
    private final long scoreChange;
    private final String ipAddress;

    // 관련 활동 목록
    private final List<RelatedActivity> relatedActivities;

    /**
     * 관련 활동 정보
     */
    @Getter
    @Builder
    public static class RelatedActivity {
        private final String activityType;
        private final String targetType;
        private final Long targetId;
        private final LocalDateTime timestamp;
        private final long scoreChange;
    }
} 