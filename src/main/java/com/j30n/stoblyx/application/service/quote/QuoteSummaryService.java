package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.application.port.in.quote.QuoteSummaryUseCase;
import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.application.port.out.quote.QuoteSummaryPort;
import com.j30n.stoblyx.domain.model.Quote;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문구 요약 서비스
 * KoBART API를 사용하여 문구를 요약하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class QuoteSummaryService implements QuoteSummaryUseCase {

    private final QuotePort quotePort;
    private final QuoteSummaryPort quoteSummaryPort;

    /**
     * 특정 문구를 요약합니다.
     *
     * @param quoteId 요약할 문구의 ID
     * @return 요약된 문구 정보
     * @throws EntityNotFoundException 문구가 존재하지 않는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public QuoteSummaryResponse summarizeQuote(Long quoteId) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("Quote not found with id: " + quoteId));
        String summary = quoteSummaryPort.summarize(quote.getContent());
        return QuoteSummaryResponse.from(quote, summary);
    }
}