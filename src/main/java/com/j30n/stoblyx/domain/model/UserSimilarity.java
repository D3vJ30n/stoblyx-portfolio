package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_similarities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSimilarity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id")
    private User sourceUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @Column(nullable = false)
    private Double similarityScore;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder
    public UserSimilarity(User sourceUser, User targetUser, Double similarityScore) {
        this.sourceUser = sourceUser;
        this.targetUser = targetUser;
        this.similarityScore = similarityScore;
        this.isActive = true;
    }

    public void updateSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
} 