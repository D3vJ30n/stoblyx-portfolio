package com.j30n.stoblyx.adapter.in.web.dto.search;

import jakarta.validation.constraints.NotBlank;

/**
 * 검색 요청을 위한 DTO
 */
public record SearchRequest(
    @NotBlank(message = "검색어를 입력해주세요.")
    String keyword,
    SearchType type,
    String category,
    String sortBy,
    String sortDirection
) {
    public SearchRequest {
        type = (type == null) ? SearchType.ALL : type;
        sortBy = (sortBy == null) ? "createdAt" : sortBy;
        sortDirection = (sortDirection == null) ? "DESC" : sortDirection;
    }
}
