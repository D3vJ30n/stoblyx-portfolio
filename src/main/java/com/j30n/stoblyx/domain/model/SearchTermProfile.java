package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "search_term_profiles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_search_term", columnNames = {"search_term"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchTermProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "search_term", nullable = false, length = 255)
    private String searchTerm;

    @Column(name = "search_count", nullable = false)
    private Integer searchCount = 0;

    @Column(name = "user_demographic_data", columnDefinition = "TEXT")
    private String userDemographicData;

    @Column(name = "related_terms", columnDefinition = "TEXT")
    private String relatedTerms;

    @Column(name = "trend_data", columnDefinition = "TEXT")
    private String trendData;

    @Builder
    public SearchTermProfile(String searchTerm, Integer searchCount, String userDemographicData, 
                            String relatedTerms, String trendData) {
        this.searchTerm = searchTerm;
        this.searchCount = searchCount != null ? searchCount : 0;
        this.userDemographicData = userDemographicData;
        this.relatedTerms = relatedTerms;
        this.trendData = trendData;
    }

    public void incrementSearchCount() {
        this.searchCount += 1;
    }

    public void updateTrendData(String trendData) {
        this.trendData = trendData;
    }

    public void updateRelatedTerms(String relatedTerms) {
        this.relatedTerms = relatedTerms;
    }

    public void updateUserDemographicData(String userDemographicData) {
        this.userDemographicData = userDemographicData;
    }
} 