package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.QuoteSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteSummaryRepository extends JpaRepository<QuoteSummary, Long> {
    
    /**
     * 인용구로 요약 정보를 찾습니다.
     */
    Optional<QuoteSummary> findByQuote(Quote quote);
    
    /**
     * 인용구 ID로 요약 정보를 찾습니다.
     */
    Optional<QuoteSummary> findByQuoteId(Long quoteId);
    
    /**
     * 인용구 ID로 요약 정보를 삭제합니다.
     */
    void deleteByQuoteId(Long quoteId);
} 