package com.j30n.stoblyx.application.port.in.content;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentUseCase {
    ContentResponse generateContent(Long quoteId);
    ContentResponse getContent(Long id);
    Page<ContentResponse> getAllContents(Pageable pageable);
    Page<ContentResponse> getContentsByUser(Long userId, Pageable pageable);
    Page<ContentResponse> getContentsByBook(Long bookId, Pageable pageable);
    Page<ContentResponse> searchContents(String keyword, Pageable pageable);
    Page<ContentResponse> getTrendingContents(Pageable pageable);
    Page<ContentResponse> getPopularContents(Pageable pageable);
    Page<ContentResponse> getRecommendedContents(Long userId, Pageable pageable);
    /**
     * 상태별 콘텐츠 목록을 조회합니다.
     *
     * @param status 콘텐츠 상태
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    Page<ContentResponse> getContentsByStatus(String status, Pageable pageable);
    void deleteContent(Long id);
    void updateContentStatus(Long id, String status);
    void incrementViewCount(Long id);
    void toggleLike(Long userId, Long contentId);
    void toggleBookmark(Long userId, Long contentId);
    void incrementShareCount(Long id);
}
