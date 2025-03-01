package com.j30n.stoblyx.application.port.in.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteSummaryResponse;

public interface QuoteSummaryUseCase {
    /**
     * 특정 문구를 요약합니다.
     *
     * @param quoteId 요약할 문구의 ID
     * @return 요약된 문구 정보
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    QuoteSummaryResponse summarizeQuote(Long quoteId);
}
