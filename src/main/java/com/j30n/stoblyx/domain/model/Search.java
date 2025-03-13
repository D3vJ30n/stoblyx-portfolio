package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 검색 기록을 저장하는 엔티티
 */
@Entity
@Table(name = "search")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Search extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(name = "search_term", length = 255, nullable = false)
    private String searchTerm;

    @Column(name = "search_count", nullable = false)
    private Integer searchCount = 1;

    @Column(name = "last_searched_at", nullable = false)
    private LocalDateTime lastSearchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "search_type", length = 50, nullable = false)
    private String searchType;

    @Builder
    public Search(String searchTerm, Integer searchCount, User user, String searchType) {
        this.searchTerm = searchTerm;
        this.searchCount = searchCount != null ? searchCount : 1;
        this.user = user;
        this.searchType = searchType;
        this.lastSearchedAt = LocalDateTime.now();
    }

    /**
     * 검색 결과 수를 업데이트합니다.
     */
    public void updateSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    /**
     * 검색 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateLastSearchedAt() {
        this.lastSearchedAt = LocalDateTime.now();
    }

    /**
     * 검색 시간을 특정 시간으로 설정합니다.
     */
    public void setLastSearchedAt(LocalDateTime lastSearchedAt) {
        this.lastSearchedAt = lastSearchedAt;
    }
} 