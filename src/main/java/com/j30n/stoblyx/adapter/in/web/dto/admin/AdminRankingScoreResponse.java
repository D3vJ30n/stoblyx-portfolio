package com.j30n.stoblyx.adapter.in.web.dto.admin;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;

import java.time.LocalDateTime;

/**
 * 관리자가 사용자 랭킹 점수 정보를 조회할 때 사용되는 응답 DTO
 */
public record AdminRankingScoreResponse(
    Long id,
    Long userId,
    Integer currentScore,
    Integer previousScore,
    RankType rankType,
    Boolean suspiciousActivity,
    Integer reportCount,
    Boolean accountSuspended,
    LocalDateTime lastActivityDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    /**
     * RankingUserScore 엔티티로부터 AdminRankingScoreResponse를 생성합니다.
     *
     * @param score 랭킹 사용자 점수 엔티티
     * @return AdminRankingScoreResponse 객체
     */
    public static AdminRankingScoreResponse from(RankingUserScore score) {
        return new AdminRankingScoreResponse(
            score.getId(),
            score.getUserId(),
            score.getCurrentScore(),
            score.getPreviousScore(),
            score.getRankType(),
            score.getSuspiciousActivity(),
            score.getReportCount(),
            score.getAccountSuspended(),
            score.getLastActivityDate(),
            score.getCreatedAt(),
            score.getUpdatedAt()
        );
    }
} 