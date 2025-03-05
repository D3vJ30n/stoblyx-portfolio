package com.j30n.stoblyx.adapter.in.web.dto.admin.stats;

import lombok.Builder;
import lombok.Getter;

/**
 * 대시보드 요약 통계 응답 객체
 */
@Getter
@Builder
public class DashboardSummaryResponse {

    // 사용자 관련 통계
    private final long totalUsers;
    private final long newUsersToday;
    private final long activeUsersToday;

    // 콘텐츠 관련 통계
    private final long totalContents;
    private final long contentsCreatedToday;
    private final long pendingContents;

    // 활동 관련 통계
    private final long totalQuotes;
    private final long totalLikes;
    private final long totalComments;
    private final long totalBookmarks;

    // 시스템 관련 통계
    private final double cpuUsage;
    private final double memoryUsage;
    private final double diskUsage;
} 