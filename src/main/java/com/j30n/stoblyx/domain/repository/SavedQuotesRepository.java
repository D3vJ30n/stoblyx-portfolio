package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.SavedQuotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 저장된 명언 JPA 리포지토리
 */
public interface SavedQuotesRepository extends JpaRepository<SavedQuotes, Long> {
    @Query("SELECT s FROM SavedQuotes s WHERE s.user.id = :userId AND s.quote.id = :quoteId AND s.isDeleted = false")
    Optional<SavedQuotes> findByUserIdAndQuoteId(@Param("userId") Long userId, @Param("quoteId") Long quoteId);

    @Query("SELECT s.quote FROM SavedQuotes s WHERE s.user.id = :userId AND s.isDeleted = false")
    Page<Quote> findQuotesByUserId(@Param("userId") Long userId, Pageable pageable);
}