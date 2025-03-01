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
@Table(name = "searches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Search extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(length = 100, nullable = false)
    private String keyword;

    @Column
    private Integer resultCount = 0;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 30)
    private String category;

    @Builder
    public Search(String keyword, Integer resultCount, User user, String category) {
        this.keyword = keyword;
        this.resultCount = resultCount != null ? resultCount : 0;
        this.user = user;
        this.category = category;
        this.searchedAt = LocalDateTime.now();
    }

    /**
     * 검색 결과 수를 업데이트합니다.
     */
    public void updateResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * 검색 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateSearchedAt() {
        this.searchedAt = LocalDateTime.now();
    }
} 