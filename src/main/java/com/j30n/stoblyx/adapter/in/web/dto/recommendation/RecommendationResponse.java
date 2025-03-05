package com.j30n.stoblyx.adapter.in.web.dto.recommendation;

import com.j30n.stoblyx.domain.model.UserSimilarity;

/**
 * 사용자 추천 응답을 위한 DTO
 */
public record RecommendationResponse(
    Long id,
    Long sourceUserId,
    Long targetUserId,
    String targetUserName,
    String targetUserProfileImage,
    Double similarityScore,
    Boolean isActive
) {
    /**
     * UserSimilarity 엔티티를 RecommendationResponse DTO로 변환합니다.
     *
     * @param similarity 사용자 유사도 엔티티
     * @return RecommendationResponse DTO
     */
    public static RecommendationResponse fromEntity(UserSimilarity similarity) {
        return new RecommendationResponse(
            similarity.getId(),
            similarity.getSourceUser().getId(),
            similarity.getTargetUser().getId(),
            similarity.getTargetUser().getUsername(),
            similarity.getTargetUser().getProfileImageUrl(),
            similarity.getSimilarityScore(),
            similarity.getIsActive()
        );
    }
} 