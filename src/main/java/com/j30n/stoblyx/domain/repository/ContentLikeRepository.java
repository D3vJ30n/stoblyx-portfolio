package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ContentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {
    boolean existsByContentIdAndUserId(Long contentId, Long userId);
    void deleteByContentIdAndUserId(Long contentId, Long userId);
}
