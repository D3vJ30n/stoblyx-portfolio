package com.j30n.stoblyx.adapter.in.web.dto.admin.stats;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 콘텐츠 생성 통계 응답 객체
 */
@Getter
@Builder
public class ContentStatsResponse {

    // 기간 정보
    private final String period;
    private final LocalDate startDate;
    private final LocalDate endDate;

    // 콘텐츠 생성 통계
    private final List<TimeSeriesDataPoint> contentCreationStats;

    // 콘텐츠 상태별 통계
    private final Map<String, Long> contentStatusStats;

    // 콘텐츠 유형별 통계
    private final Map<String, Long> contentTypeStats;

    // 인기 콘텐츠 통계
    private final List<PopularContentStats> popularContents;

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
     * 인기 콘텐츠 통계
     */
    @Getter
    @Builder
    public static class PopularContentStats {
        private final Long contentId;
        private final String title;
        private final long viewCount;
        private final long likeCount;
        private final long commentCount;
        private final long shareCount;
    }
} 