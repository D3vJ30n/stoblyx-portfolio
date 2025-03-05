package com.j30n.stoblyx.application.port.in.recommendation;

import com.j30n.stoblyx.adapter.in.web.dto.recommendation.PopularTermResponse;
import com.j30n.stoblyx.adapter.in.web.dto.recommendation.RecommendationRequest;
import com.j30n.stoblyx.adapter.in.web.dto.recommendation.RecommendationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecommendationUseCase {
    
    /**
     * 사용자에게 추천 사용자 목록을 제공합니다.
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 추천 사용자 목록
     */
    Page<RecommendationResponse> getUserRecommendations(Long userId, Pageable pageable);
    
    /**
     * 협업 필터링 알고리즘을 실행하여 사용자 추천 정보를 갱신합니다.
     *
     * @param request 추천 요청 정보
     * @return 갱신된 추천 수
     */
    Integer runCollaborativeFiltering(RecommendationRequest request);
    
    /**
     * 특정 사용자의 검색어 기반 추천을 갱신합니다.
     *
     * @param userId 사용자 ID
     * @return 갱신된 추천 수
     */
    Integer updateUserRecommendations(Long userId);
    
    /**
     * 현재 인기 검색어 목록을 제공합니다.
     *
     * @param pageable 페이징 정보
     * @return 인기 검색어 목록
     */
    Page<PopularTermResponse> getPopularTerms(Pageable pageable);
    
    /**
     * 인기 검색어 분석을 실행합니다.
     *
     * @return 갱신된 인기 검색어 수
     */
    Integer updatePopularTerms();
} 