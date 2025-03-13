package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "popular_search_terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularSearchTerm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "search_term", nullable = false, length = 255)
    private String searchTerm;

    @Column(name = "search_count", nullable = false)
    private Integer searchCount;

    @Column(name = "popularity_score", nullable = false)
    private Double popularityScore;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Builder
    public PopularSearchTerm(String searchTerm, Integer searchCount, Double popularityScore) {
        this.searchTerm = searchTerm;
        this.searchCount = searchCount != null ? searchCount : 1;
        this.popularityScore = popularityScore != null ? popularityScore : 1.0;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void incrementSearchCount() {
        this.searchCount += 1;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public void updatePopularityScore(Double popularityScore) {
        this.popularityScore = popularityScore;
        this.lastUpdatedAt = LocalDateTime.now();
    }
} 