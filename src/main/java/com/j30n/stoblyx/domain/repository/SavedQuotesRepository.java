package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.SavedQuotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedQuotesRepository extends JpaRepository<SavedQuotes, Long> {
    Optional<SavedQuotes> findByUserIdAndQuoteId(Long userId, Long quoteId);
    boolean existsByUserIdAndQuoteId(Long userId, Long quoteId);
    Page<SavedQuotes> findByUserId(Long userId, Pageable pageable);
} 