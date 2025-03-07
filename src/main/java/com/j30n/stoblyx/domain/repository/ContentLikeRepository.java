package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ContentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {
    boolean existsByContentIdAndUserId(Long contentId, Long userId);
    void deleteByContentIdAndUserId(Long contentId, Long userId);
    
    // 특정 콘텐츠의 좋아요 수 조회
    long countByContentId(Long contentId);
    
    // 특정 기간 내 생성된 좋아요 수 조회
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 특정 사용자의 특정 기간 내 생성된 좋아요 수 조회
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    // 특정 사용자의 좋아요 수 조회
    long countByUserId(Long userId);
}
