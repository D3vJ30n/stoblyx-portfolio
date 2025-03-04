package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.AchievementStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 랭킹 업적 엔티티
 */
@Entity
@Table(name = "ranking_achievements")
@Data
public class RankingAchievement {
    
    /**
     * 업적 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 사용자 ID
     */
    @Column(nullable = false)
    private Long userId;
    
    /**
     * 업적 코드
     */
    @Column(nullable = false)
    private String achievementCode;
    
    /**
     * 업적 이름
     */
    @Column(nullable = false)
    private String achievementName;
    
    /**
     * 업적 설명
     */
    @Column(length = 500)
    private String description;
    
    /**
     * 업적 달성 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementStatus status;
    
    /**
     * 업적 달성 일시
     */
    private LocalDateTime achievedAt;
    
    /**
     * 업적 만료 일시
     */
    private LocalDateTime expiryDate;
    
    /**
     * 업적 생성 일시
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 업적 수정 일시
     */
    private LocalDateTime updatedAt;
    
    /**
     * 업적 달성 진행도 (0-100)
     */
    private Integer progressPercentage;
    
    /**
     * 업적 달성 시 보상 점수
     */
    private Integer rewardPoints;
    
    /**
     * 업적 달성 완료 처리
     */
    public void complete() {
        this.status = AchievementStatus.COMPLETED;
        this.achievedAt = LocalDateTime.now();
        this.progressPercentage = 100;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 업적 만료 처리
     */
    public void expire() {
        this.status = AchievementStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 업적 진행도 업데이트
     * 
     * @param progressPercentage 진행도 (0-100)
     */
    public void updateProgress(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
        this.updatedAt = LocalDateTime.now();
        
        if (progressPercentage >= 100) {
            complete();
        }
    }
} 