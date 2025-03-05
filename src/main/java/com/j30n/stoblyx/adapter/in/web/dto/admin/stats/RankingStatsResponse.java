package com.j30n.stoblyx.adapter.in.web.dto.admin.stats;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 랭킹 시스템 통계 응답 객체
 */
@Getter
@Builder
public class RankingStatsResponse {

    // 랭크별 사용자 분포
    private final Map<String, Long> rankDistribution;

    // 점수 분포 통계
    private final ScoreDistributionStats scoreDistribution;

    // 랭크 변경 통계
    private final List<RankChangeStats> recentRankChanges;

    // 활동 패턴 분석
    private final Map<String, Long> activityPatternStats;

    // 상위 랭킹 사용자
    private final List<TopRankedUserStats> topRankedUsers;

    /**
     * 점수 분포 통계
     */
    @Getter
    @Builder
    public static class ScoreDistributionStats {
        private final double averageScore;
        private final double medianScore;
        private final long minScore;
        private final long maxScore;
        private final Map<String, Long> scoreRanges;
    }

    /**
     * 랭크 변경 통계
     */
    @Getter
    @Builder
    public static class RankChangeStats {
        private final Long userId;
        private final String username;
        private final String previousRank;
        private final String currentRank;
        private final long scoreChange;
        private final String changeDate;
    }

    /**
     * 상위 랭킹 사용자 통계
     */
    @Getter
    @Builder
    public static class TopRankedUserStats {
        private final Long userId;
        private final String username;
        private final String rank;
        private final long score;
        private final long contentCount;
        private final long likeCount;
        private final long commentCount;
    }
} 