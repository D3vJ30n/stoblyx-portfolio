package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.deleted = false")
    Optional<Comment> findByIdAndDeletedFalse(@Param("id") Long id);

    @Query("SELECT c FROM Comment c WHERE c.quote.id = :quoteId AND c.deleted = false")
    Page<Comment> findByQuoteId(@Param("quoteId") Long quoteId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.deleted = false")
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);
} 