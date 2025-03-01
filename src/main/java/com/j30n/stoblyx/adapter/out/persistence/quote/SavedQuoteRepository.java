package com.j30n.stoblyx.adapter.out.persistence.quote;

import com.j30n.stoblyx.domain.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * 저장된 명언 리포지토리
 */
@Repository
public interface SavedQuoteRepository {
    void save(Long userId, Long quoteId, String note);
    void delete(Long userId, Long quoteId);
    Page<Quote> findByUserId(Long userId, Pageable pageable);
}
