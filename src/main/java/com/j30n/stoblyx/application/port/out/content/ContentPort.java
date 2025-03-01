package com.j30n.stoblyx.application.port.out.content;

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
}
