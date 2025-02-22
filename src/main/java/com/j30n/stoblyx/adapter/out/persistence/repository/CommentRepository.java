package com.j30n.stoblyx.adapter.out.persistence.repository;

import com.j30n.stoblyx.adapter.out.persistence.entity.CommentJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.QuoteJpaEntity;
import com.j30n.stoblyx.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 댓글 JPA 리포지토리
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentJpaEntity, Long> {
    /**
     * 인용구에 달린 댓글 목록을 조회합니다.
     */
    List<CommentJpaEntity> findByQuoteOrderByCreatedAtDesc(QuoteJpaEntity quote);

    /**
     * 사용자가 작성한 댓글 목록을 조회합니다.
     */
    List<CommentJpaEntity> findByUserOrderByCreatedAtDesc(UserJpaEntity user);

    /**
     * 인용구에 달린 댓글 수를 조회합니다.
     */
    @Query("SELECT COUNT(c) FROM CommentJpaEntity c WHERE c.quote = :quote AND c.isDeleted = false")
    long countByQuoteAndIsDeletedFalse(QuoteJpaEntity quote);
} 