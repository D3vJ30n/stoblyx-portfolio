package com.j30n.stoblyx.application.port.out.quote;

import com.j30n.stoblyx.domain.model.QuoteSummary;

import java.util.Optional;

public interface QuoteSummaryPort {
    /**
     * KoBART API를 사용하여 문구를 요약합니다.
     *
     * @param content 요약할 문구 내용
     * @return 요약된 문구
     */
    String summarize(String content);
    
    /**
     * 인용구의 요약 정보를 저장합니다.
     *
     * @param quoteId 인용구 ID
     * @param content 요약 내용
     * @return 저장된 요약 정보
     */
    QuoteSummary saveQuoteSummary(Long quoteId, String content);
    
    /**
     * 인용구 ID로 요약 정보를 조회합니다.
     *
     * @param quoteId 인용구 ID
     * @return 요약 정보
     */
    Optional<QuoteSummary> findByQuoteId(Long quoteId);
    
    /**
     * 인용구 ID로 요약 정보를 삭제합니다.
     *
     * @param quoteId 인용구 ID
     */
    void deleteByQuoteId(Long quoteId);
}
