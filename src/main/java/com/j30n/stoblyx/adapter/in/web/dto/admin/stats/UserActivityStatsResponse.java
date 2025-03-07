package com.j30n.stoblyx.adapter.in.web.dto.admin.stats;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 사용자 활동 통계 응답 객체
 */
@Getter
@Builder
public class UserActivityStatsResponse {

    // 기간 정보
    private final String period;
    private final LocalDate startDate;
    private final LocalDate endDate;

    // 신규 가입 통계
    private final List<TimeSeriesDataPoint> newUserStats;

    // 로그인 통계
    private final List<TimeSeriesDataPoint> loginStats;

    // 콘텐츠 생성 통계
    private final List<TimeSeriesDataPoint> contentCreationStats;

    // 활동 유형별 통계
    private final Map<String, Long> activityTypeStats;

    // 활동적인 사용자 통계
    private final List<ActiveUserStats> activeUsers;

    /**
     * 시계열 데이터 포인트
     */
    @Getter
    @Builder
    public static class TimeSeriesDataPoint {
        private final String date;
        private final long count;
    }

    /**
     * 활동적인 사용자 통계
     */
    @Getter
    @Builder
    public static class ActiveUserStats {
        private final Long userId;
        private final String username;
        private final long contentCount;
        private final long likeCount;
        private final long commentCount;
        private final long loginCount;
        private final long totalScore;
        private final long activityScore;
    }
}