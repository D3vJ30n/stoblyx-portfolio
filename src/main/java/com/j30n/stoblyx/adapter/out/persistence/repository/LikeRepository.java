package com.j30n.stoblyx.adapter.out.persistence.repository;

import com.j30n.stoblyx.adapter.out.persistence.entity.LikeJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.QuoteJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 좋아요 JPA 리포지토리
 */
@Repository
public interface LikeRepository extends JpaRepository<LikeJpaEntity, Long> {
    /**
     * 인용구에 달린 좋아요 목록을 조회합니다.
     */
    List<LikeJpaEntity> findByQuoteOrderByCreatedAtDesc(QuoteJpaEntity quote);

    /**
     * 사용자가 누른 좋아요 목록을 조회합니다.
     */
    List<LikeJpaEntity> findByUserOrderByCreatedAtDesc(UserJpaEntity user);

    /**
     * 사용자가 인용구에 누른 좋아요를 조회합니다.
     */
    Optional<LikeJpaEntity> findByQuoteAndUser(Optional<QuoteJpaEntity> quote, UserJpaEntity user);

    /**
     * 인용구의 활성화된 좋아요 수를 조회합니다.
     */
    @Query("SELECT COUNT(l) FROM LikeJpaEntity l WHERE l.quote = :quote AND l.isActive = true")
    long countByQuoteAndIsActiveTrue(QuoteJpaEntity quote);
} 