package com.j30n.stoblyx.adapter.out.persistence.quote;

import com.j30n.stoblyx.adapter.out.persistence.ai.KoBartClient;
import com.j30n.stoblyx.application.port.out.quote.QuoteSummaryPort;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.QuoteSummary;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.QuoteSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuoteSummaryPersistenceAdapter implements QuoteSummaryPort {

    private final KoBartClient koBartClient;
    private final QuoteRepository quoteRepository;
    private final QuoteSummaryRepository quoteSummaryRepository;

    @Override
    public String summarize(String content) {
        return koBartClient.summarize(content);
    }
    
    @Override
    @Transactional
    public QuoteSummary saveQuoteSummary(Long quoteId, String content) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다."));
        
        // 기존 요약 정보가 있는지 확인
        Optional<QuoteSummary> existingSummary = quoteSummaryRepository.findByQuote(quote);
        
        if (existingSummary.isPresent()) {
            // 기존 요약 정보 업데이트
            QuoteSummary summary = existingSummary.get();
            summary.updateContent(content, "KoBART");
            return quoteSummaryRepository.save(summary);
        } else {
            // 새 요약 정보 생성
            QuoteSummary summary = QuoteSummary.builder()
                    .quote(quote)
                    .content(content)
                    .algorithm("KoBART")
                    .quality(0.0)
                    .build();
            return quoteSummaryRepository.save(summary);
        }
    }
    
    @Override
    public Optional<QuoteSummary> findByQuoteId(Long quoteId) {
        return quoteSummaryRepository.findByQuoteId(quoteId);
    }
    
    @Override
    @Transactional
    public void deleteByQuoteId(Long quoteId) {
        quoteSummaryRepository.deleteByQuoteId(quoteId);
    }
}
