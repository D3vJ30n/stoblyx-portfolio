package com.j30n.stoblyx.application.port.in.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.SavedQuoteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteUseCase {
    QuoteResponse createQuote(Long userId, QuoteCreateRequest request);
    QuoteResponse getQuote(Long quoteId, Long userId);
    Page<QuoteResponse> getQuotes(Long userId, Pageable pageable);
    QuoteResponse updateQuote(Long quoteId, Long userId, QuoteUpdateRequest request);
    void deleteQuote(Long quoteId, Long userId);
    QuoteResponse saveQuote(Long userId, Long quoteId, SavedQuoteRequest request);
    void unsaveQuote(Long userId, Long quoteId);
    Page<QuoteResponse> getSavedQuotes(Long userId, Pageable pageable);
}
