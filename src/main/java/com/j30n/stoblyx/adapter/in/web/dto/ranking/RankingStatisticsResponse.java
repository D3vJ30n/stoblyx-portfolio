package com.j30n.stoblyx.adapter.in.web.dto.ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 랭킹 통계 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingStatisticsResponse {
    /**
     * 랭크 타입별 사용자 분포
     * 키: 랭크 타입 (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND)
     * 값: 해당 랭크 타입의 사용자 수
     */
    private Map<String, Long> rankDistribution;
    
    /**
     * 전체 사용자의 평균 점수
     */
    private Double averageScore;
} 