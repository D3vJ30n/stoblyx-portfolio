package com.j30n.stoblyx.adapter.in.web.dto.admin;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.RankType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 관리자가 랭킹 시스템 통계를 조회할 때 사용되는 응답 DTO
 */
public record AdminRankingStatisticsResponse(
    Map<RankType, Long> rankDistribution,
    Map<ActivityType, Long> activityTypeDistribution,
    Map<Integer, Long> activityByHour,
    Long totalUsers,
    Long activeUsers,
    Long suspendedUsers,
    Double averageScore,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
    /**
     * 생성자
     *
     * @param rankDistribution 랭크 타입별 사용자 분포
     * @param activityTypeDistribution 활동 유형별 분포
     * @param activityByHour 시간대별 활동 분포
     * @param totalUsers 전체 사용자 수
     * @param activeUsers 활성 사용자 수
     * @param suspendedUsers 정지된 사용자 수
     * @param averageScore 평균 점수
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     */
    public AdminRankingStatisticsResponse {
        if (rankDistribution == null || activityTypeDistribution == null || activityByHour == null) {
            throw new IllegalArgumentException("통계 데이터는 null일 수 없습니다.");
        }
    }
}