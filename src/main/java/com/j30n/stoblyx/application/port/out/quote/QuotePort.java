package com.j30n.stoblyx.application.port.out.quote;

import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QuotePort {
    Quote save(Quote quote);
    Optional<Quote> findQuoteById(Long id);
    Page<Quote> findByUserId(Long userId, Pageable pageable);
    void delete(Quote quote);
    boolean existsByIdAndUserId(Long id, Long userId);
    void saveQuoteToUser(Long userId, Long quoteId, String note);
    void unsaveQuoteFromUser(Long userId, Long quoteId);
    Page<Quote> findSavedQuotesByUserId(Long userId, Pageable pageable);
    void likeQuote(User user, Quote quote);
    void unlikeQuote(Long userId, Long quoteId);
}
