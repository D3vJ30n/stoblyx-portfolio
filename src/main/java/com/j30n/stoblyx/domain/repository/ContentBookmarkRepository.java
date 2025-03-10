package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ContentBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ContentBookmarkRepository extends JpaRepository<ContentBookmark, Long> {
    Optional<ContentBookmark> findByUserIdAndContentId(Long userId, Long contentId);
    boolean existsByUserIdAndContentId(Long userId, Long contentId);
    void deleteByUserIdAndContentId(Long userId, Long contentId);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Page<ContentBookmark> findByUserId(Long userId, Pageable pageable);
}
