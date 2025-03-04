package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.RankType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 랭킹 리더보드 정보를 저장하는 엔티티
 * 주간/월간 랭킹 정보를 관리
 */
@Entity
@Table(name = "ranking_leaderboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingLeaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type", nullable = false)
    private RankType rankType;

    @Column(name = "leaderboard_type", nullable = false)
    private String leaderboardType; // DAILY, WEEKLY, MONTHLY

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "period_start_date", nullable = false)
    private LocalDateTime periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDateTime periodEndDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // 기본값 설정
        if (rankType == null) {
            rankType = RankType.fromScore(score);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // 점수에 따른 랭크 업데이트
        rankType = RankType.fromScore(score);
    }
} 