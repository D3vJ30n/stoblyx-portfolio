package com.j30n.stoblyx.application.port.out.content;

import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContentPort {
    ShortFormContent save(ShortFormContent content);
    Optional<ShortFormContent> findById(Long id);
    void delete(ShortFormContent content);
    Page<ShortFormContent> findAll(Pageable pageable);
    Page<ShortFormContent> findByUserId(Long userId, Pageable pageable);
    Page<ShortFormContent> findByBookId(Long bookId, Pageable pageable);
    Page<ShortFormContent> search(String keyword, Pageable pageable);
    Page<ShortFormContent> findTrendingContents(Pageable pageable);
    Page<ShortFormContent> findPopularContents(Pageable pageable);
    Page<ShortFormContent> findRecommendedContents(Long userId, Pageable pageable);
    boolean isLikedByUser(Long contentId, Long userId);
    void updateStatus(Long id, String status);
    /**
     * 상태별 콘텐츠 목록을 조회합니다.
     *
     * @param status 콘텐츠 상태
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    Page<ShortFormContent> findByStatus(ContentStatus status, Pageable pageable);
    
    /**
     * 콘텐츠 상호작용을 저장합니다.
     *
     * @param userId 사용자 ID
     * @param contentId 콘텐츠 ID
     * @param interactionType 상호작용 유형
     */
    void saveInteraction(Long userId, Long contentId, String interactionType);
}
