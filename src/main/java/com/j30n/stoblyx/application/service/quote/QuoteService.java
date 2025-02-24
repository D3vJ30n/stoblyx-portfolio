package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.SavedQuoteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteService {
    QuoteResponse createQuote(Long userId, QuoteCreateRequest request);

    QuoteResponse getQuote(Long id);

    Page<QuoteResponse> getQuotes(Long userId, Long bookId, Pageable pageable);

    QuoteResponse updateQuote(Long userId, Long quoteId, QuoteCreateRequest request);

    void deleteQuote(Long userId, Long quoteId);

    QuoteResponse saveQuote(Long userId, Long quoteId, SavedQuoteRequest request);

    void unsaveQuote(Long userId, Long quoteId);

    Page<QuoteResponse> getSavedQuotes(Long userId, Pageable pageable);
} 