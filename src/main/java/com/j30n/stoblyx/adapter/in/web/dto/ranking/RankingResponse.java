package com.j30n.stoblyx.adapter.in.web.dto.ranking;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 랭킹 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private Long userId;
    private Integer score;
    private RankType rankType;
    private String rankDisplayName;
    private LocalDateTime lastActivityDate;
    private Boolean suspiciousActivity;
    private Integer reportCount;
    private Boolean accountSuspended;

    /**
     * RankingUserScore 엔티티를 RankingResponse DTO로 변환
     *
     * @param entity RankingUserScore 엔티티
     * @return RankingResponse DTO
     */
    public static RankingResponse fromEntity(RankingUserScore entity) {
        if (entity == null) {
            return null;
        }
        
        return RankingResponse.builder()
            .userId(entity.getUserId())
            .score(entity.getCurrentScore())
            .rankType(entity.getRankType())
            .rankDisplayName(entity.getRankType().getDisplayName())
            .lastActivityDate(entity.getLastActivityDate())
            .suspiciousActivity(entity.getSuspiciousActivity())
            .reportCount(entity.getReportCount())
            .accountSuspended(entity.getAccountSuspended())
            .build();
    }
} 