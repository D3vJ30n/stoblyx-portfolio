package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 게이미피케이션 보상 정보를 저장하는 엔티티
 * 사용자의 랭크에 따른 보상 내역을 관리
 */
@Entity
@Table(name = "gamification_reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type", nullable = false)
    private RankType rankType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @Column(name = "reward_amount")
    private Integer rewardAmount;

    @Column(name = "reward_description", nullable = false)
    private String rewardDescription;

    @Column(name = "is_claimed", nullable = false)
    private Boolean isClaimed;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // 기본값 설정
        if (isClaimed == null) {
            isClaimed = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 보상 지급 처리
     */
    public void claim() {
        if (isClaimed) {
            throw new IllegalStateException("Reward already claimed");
        }
        
        isClaimed = true;
        claimedAt = LocalDateTime.now();
    }

    /**
     * 보상이 만료되었는지 확인
     * 
     * @return 만료 여부
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        
        return LocalDateTime.now().isAfter(expiryDate);
    }
} 