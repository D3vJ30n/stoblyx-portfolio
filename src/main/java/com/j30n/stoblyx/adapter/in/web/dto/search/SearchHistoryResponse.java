package com.j30n.stoblyx.adapter.in.web.dto.search;

import com.j30n.stoblyx.domain.model.Search;

import java.time.LocalDateTime;

/**
 * 검색 기록 응답을 위한 DTO
 */
public record SearchHistoryResponse(
    Long id,
    String keyword,
    String category,
    Integer resultCount,
    LocalDateTime searchedAt,
    Long userId
) {
    /**
     * Search 엔티티를 SearchHistoryResponse DTO로 변환합니다.
     *
     * @param search 검색 기록 엔티티
     * @return SearchHistoryResponse DTO
     */
    public static SearchHistoryResponse fromEntity(Search search) {
        return new SearchHistoryResponse(
            search.getId(),
            search.getSearchTerm(),
            search.getSearchType(),
            search.getSearchCount(),
            search.getLastSearchedAt(),
            search.getUser() != null ? search.getUser().getId() : null
        );
    }
} 