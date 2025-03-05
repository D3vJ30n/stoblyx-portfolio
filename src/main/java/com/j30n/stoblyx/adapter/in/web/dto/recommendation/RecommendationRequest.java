package com.j30n.stoblyx.adapter.in.web.dto.recommendation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 추천 시스템 요청을 위한 DTO
 */
public record RecommendationRequest(
    @Min(value = 0, message = "유사도 임계값은 0 이상이어야 합니다.")
    @Max(value = 1, message = "유사도 임계값은 1 이하이어야 합니다.")
    Double similarityThreshold,

    Integer maxRecommendations,

    Boolean includeInactive
) {
    public RecommendationRequest {
        similarityThreshold = (similarityThreshold == null) ? 0.3 : similarityThreshold;
        maxRecommendations = (maxRecommendations == null) ? 10 : maxRecommendations;
        includeInactive = includeInactive != null && includeInactive;
    }

    /**
     * RecommendationRequest 빌더 생성
     *
     * @return RecommendationRequestBuilder 인스턴스
     */
    public static RecommendationRequestBuilder builder() {
        return new RecommendationRequestBuilder();
    }

    /**
     * RecommendationRequest를 위한 빌더 클래스
     */
    public static class RecommendationRequestBuilder {
        private Double similarityThreshold;
        private Integer maxRecommendations;
        private Boolean includeInactive;

        public RecommendationRequestBuilder similarityThreshold(Double similarityThreshold) {
            this.similarityThreshold = similarityThreshold;
            return this;
        }

        public RecommendationRequestBuilder maxRecommendations(Integer maxRecommendations) {
            this.maxRecommendations = maxRecommendations;
            return this;
        }

        public RecommendationRequestBuilder includeInactive(Boolean includeInactive) {
            this.includeInactive = includeInactive;
            return this;
        }

        /**
         * RecommendationRequest 객체 생성
         *
         * @return 새로운 RecommendationRequest 인스턴스
         */
        public RecommendationRequest build() {
            return new RecommendationRequest(similarityThreshold, maxRecommendations, includeInactive);
        }
    }
} 