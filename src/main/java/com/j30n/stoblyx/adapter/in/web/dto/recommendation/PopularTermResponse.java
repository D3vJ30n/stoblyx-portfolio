package com.j30n.stoblyx.adapter.in.web.dto.recommendation;

import com.j30n.stoblyx.domain.model.PopularSearchTerm;

import java.time.LocalDateTime;

/**
 * 인기 검색어 응답을 위한 DTO
 */
public record PopularTermResponse(
    Long id,
    String searchTerm,
    Integer searchCount,
    Double popularityScore,
    LocalDateTime lastUpdatedAt
) {
    /**
     * PopularSearchTerm 엔티티를 PopularTermResponse DTO로 변환합니다.
     *
     * @param popularTerm 인기 검색어 엔티티
     * @return PopularTermResponse DTO
     */
    public static PopularTermResponse fromEntity(PopularSearchTerm popularTerm) {
        return new PopularTermResponse(
            popularTerm.getId(),
            popularTerm.getSearchTerm(),
            popularTerm.getSearchCount(),
            popularTerm.getPopularityScore(),
            popularTerm.getLastUpdatedAt()
        );
    }
} 