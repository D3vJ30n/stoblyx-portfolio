package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 랭킹 뱃지 엔티티
 */
@Entity
@Table(name = "ranking_badge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingBadge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "badge_type", nullable = false, length = 50)
    private String badgeType;

    @Column(name = "requirement_type", nullable = false, length = 50)
    private String requirementType;

    @Column(name = "threshold_value", nullable = false)
    private Integer thresholdValue;

    @Builder.Default
    @Column(name = "points_awarded", nullable = false)
    private Integer pointsAwarded = 0;
} 