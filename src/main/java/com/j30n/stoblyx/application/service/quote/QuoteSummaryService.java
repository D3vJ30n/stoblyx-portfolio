package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.infrastructure.client.KoBartClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문구 요약 서비스
 * KoBART API를 사용하여 문구를 요약하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class QuoteSummaryService {

    private final QuoteRepository quoteRepository;
    private final KoBartClient koBartClient;

    /**
     * 특정 문구를 요약합니다.
     *
     * @param quoteId 요약할 문구의 ID
     * @return 요약된 문구 정보
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public QuoteSummaryResponse summarizeQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("Quote not found with id: " + quoteId));

        String summary = koBartClient.summarize(quote.getContent());

        return new QuoteSummaryResponse(
            quote.getId(),
            quote.getContent(),
            summary,
            quote.getBook().getTitle(),
            quote.getUser().getNickname()
        );
    }
} 