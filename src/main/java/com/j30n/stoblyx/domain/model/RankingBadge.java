package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.BadgeRarity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 랭킹 뱃지 엔티티
 */
@Entity
@Table(name = "ranking_badges")
@Data
public class RankingBadge {
    
    /**
     * 뱃지 ID
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
     * 뱃지 코드
     */
    @Column(nullable = false)
    private String badgeCode;
    
    /**
     * 뱃지 이름
     */
    @Column(nullable = false)
    private String badgeName;
    
    /**
     * 뱃지 설명
     */
    @Column(length = 500)
    private String description;
    
    /**
     * 뱃지 이미지 URL
     */
    private String imageUrl;
    
    /**
     * 뱃지 희귀도
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeRarity rarity;
    
    /**
     * 뱃지 획득 일시
     */
    private LocalDateTime acquiredAt;
    
    /**
     * 뱃지 만료 일시
     */
    private LocalDateTime expiryDate;
    
    /**
     * 뱃지 생성 일시
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 뱃지 수정 일시
     */
    private LocalDateTime updatedAt;
    
    /**
     * 뱃지 포인트 가치
     */
    private Integer pointValue;
    
    /**
     * 뱃지 획득 처리
     */
    public void acquire() {
        this.acquiredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 뱃지 만료 처리
     */
    public void expire() {
        this.expiryDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 