package com.j30n.stoblyx.application.port.in.recommendation;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 콘텐츠 기반 추천 시스템을 위한 인터페이스
 */
public interface ContentBasedRecommendationUseCase {
    
    /**
     * 사용자의 검색 기록 및 상호작용 기반 개인화된 도서 추천을 제공합니다.
     * 
     * @param pageable 페이징 정보
     * @return 개인화된 도서 추천 목록
     */
    Page<BookResponse> getPersonalizedBookRecommendations(Pageable pageable);
    
    /**
     * 특정 책과 유사한 책 목록을 추천합니다.
     * 
     * @param bookId 기준 책 ID
     * @param pageable 페이징 정보
     * @return 유사한 책 목록
     */
    Page<BookResponse> getSimilarBooks(Long bookId, Pageable pageable);
} 