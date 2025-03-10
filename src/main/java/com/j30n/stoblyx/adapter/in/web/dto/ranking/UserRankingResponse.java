package com.j30n.stoblyx.adapter.in.web.dto.ranking;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 랭킹 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingResponse {
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 현재 점수
     */
    private Integer score;
    
    /**
     * 랭크 타입
     */
    private RankType rankType;
    
    /**
     * 랭크 표시 이름
     */
    private String rankDisplayName;
    
    /**
     * 전체 랭킹 (순위)
     */
    private Integer rank;
    
    /**
     * 다음 랭크까지 필요한 점수
     */
    private Integer pointsToNextRank;
    
    /**
     * RankingUserScore 엔티티와 순위 정보를 UserRankingResponse DTO로 변환
     *
     * @param entity RankingUserScore 엔티티
     * @param rank 사용자 순위
     * @return UserRankingResponse DTO
     */
    public static UserRankingResponse fromEntity(RankingUserScore entity, Integer rank) {
        if (entity == null) {
            return null;
        }
        
        // 다음 랭크까지 필요한 점수 계산
        Integer pointsToNextRank = null;
        if (entity.getRankType() != RankType.DIAMOND) {
            RankType nextRank = getNextRankType(entity.getRankType());
            pointsToNextRank = nextRank.getMinScore() - entity.getCurrentScore();
        }
        
        return UserRankingResponse.builder()
            .userId(entity.getUserId())
            .score(entity.getCurrentScore())
            .rankType(entity.getRankType())
            .rankDisplayName(entity.getRankType().getDisplayName())
            .rank(rank)
            .pointsToNextRank(pointsToNextRank)
            .build();
    }
    
    /**
     * 다음 랭크 타입 반환
     *
     * @param currentRankType 현재 랭크 타입
     * @return 다음 랭크 타입
     */
    private static RankType getNextRankType(RankType currentRankType) {
        switch (currentRankType) {
            case BRONZE:
                return RankType.SILVER;
            case SILVER:
                return RankType.GOLD;
            case GOLD:
                return RankType.PLATINUM;
            case PLATINUM:
                return RankType.DIAMOND;
            default:
                return RankType.DIAMOND;
        }
    }
} 