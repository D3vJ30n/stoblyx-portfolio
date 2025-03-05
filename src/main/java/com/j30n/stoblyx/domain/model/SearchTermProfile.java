package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "search_term_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchTermProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String searchTerm;

    @Column(nullable = false)
    private Integer searchFrequency;

    @Column(nullable = false)
    private Double termWeight;

    @Builder
    public SearchTermProfile(User user, String searchTerm, Integer searchFrequency, Double termWeight) {
        this.user = user;
        this.searchTerm = searchTerm;
        this.searchFrequency = searchFrequency != null ? searchFrequency : 1;
        this.termWeight = termWeight != null ? termWeight : 1.0;
    }

    public void incrementFrequency() {
        this.searchFrequency += 1;
    }

    public void updateWeight(Double weight) {
        this.termWeight = weight;
    }
} 