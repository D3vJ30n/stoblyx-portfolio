package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 랭킹 업적 달성 엔티티
 */
@Entity
@Table(
    name = "ranking_achievement",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_badge", columnNames = {"user_id", "badge_id"})
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingAchievement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private RankingBadge badge;
    
    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;
    
    /**
     * 업적 달성 시간을 업데이트합니다.
     */
    public void updateAchievedAt(LocalDateTime achievedAt) {
        this.achievedAt = achievedAt;
    }
} 