package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게이미피케이션 보상 정보를 저장하는 엔티티
 * 사용자의 활동에 따른 보상 내역을 관리
 */
@Entity
@Table(name = "gamification_rewards")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationReward extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @Builder.Default
    @Column(name = "points", nullable = false)
    private Integer points = 0;

    @Column(name = "description")
    private String description;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Builder.Default
    @Column(name = "is_claimed", nullable = false)
    private Boolean isClaimed = false;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    /**
     * 보상이 이미 지급되었는지 확인
     * 
     * @return 지급 여부
     */
    public boolean isClaimed() {
        return isClaimed != null && isClaimed;
    }

    /**
     * 보상 지급 처리
     */
    public void claim() {
        if (isClaimed()) {
            throw new IllegalStateException("Reward already claimed");
        }
        
        isClaimed = true;
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