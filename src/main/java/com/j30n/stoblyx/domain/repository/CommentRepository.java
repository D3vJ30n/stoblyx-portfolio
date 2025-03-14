package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 댓글 JPA 리포지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.quote.id = :quoteId AND c.isDeleted = false")
    Page<Comment> findByQuoteId(@Param("quoteId") Long quoteId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.isDeleted = false")
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Comment c WHERE c.id = :id AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}