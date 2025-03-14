package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.quote.id = :quoteId AND l.isDeleted = false")
    Optional<Like> findByUserIdAndQuoteId(@Param("userId") Long userId, @Param("quoteId") Long quoteId);

    @Query("SELECT l FROM Like l WHERE l.quote.id = :quoteId AND l.isDeleted = false")
    Page<Like> findByQuoteId(@Param("quoteId") Long quoteId, Pageable pageable);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.isDeleted = false")
    List<Like> findByUserId(@Param("userId") Long userId);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.isDeleted = false")
    Page<Like> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.quote.id = :quoteId AND l.isDeleted = false")
    long countByQuoteId(@Param("quoteId") Long quoteId);

    boolean existsByUserIdAndQuoteId(Long userId, Long quoteId);
}