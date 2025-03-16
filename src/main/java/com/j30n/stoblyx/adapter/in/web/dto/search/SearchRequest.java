package com.j30n.stoblyx.adapter.in.web.dto.search;

/**
 * 검색 요청을 위한 DTO
 */
public record SearchRequest(
    String keyword,
    SearchType type,
    String category,
    String sortBy,
    String sortDirection,
    Long userId
) {
    public SearchRequest {
        type = (type == null) ? SearchType.ALL : type;
        sortBy = (sortBy == null) ? "createdAt" : sortBy;
        sortDirection = (sortDirection == null) ? "DESC" : sortDirection;
    }

    /**
     * SearchRequest 빌더 생성
     *
     * @return SearchRequestBuilder 인스턴스
     */
    public static SearchRequestBuilder builder() {
        return new SearchRequestBuilder();
    }

    /**
     * SearchRequest를 위한 빌더 클래스
     */
    public static class SearchRequestBuilder {
        private String keyword;
        private SearchType type;
        private String category;
        private String sortBy;
        private String sortDirection;
        private Long userId;

        public SearchRequestBuilder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public SearchRequestBuilder type(SearchType type) {
            this.type = type;
            return this;
        }

        public SearchRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SearchRequestBuilder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public SearchRequestBuilder sortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }

        public SearchRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        /**
         * SearchRequest 객체 생성
         *
         * @return 새로운 SearchRequest 인스턴스
         */
        public SearchRequest build() {
            return new SearchRequest(keyword, type, category, sortBy, sortDirection, userId);
        }
    }
}
