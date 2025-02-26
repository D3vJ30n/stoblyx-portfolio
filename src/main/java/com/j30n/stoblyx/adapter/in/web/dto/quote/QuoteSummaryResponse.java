package com.j30n.stoblyx.adapter.in.web.dto.quote;

import com.j30n.stoblyx.domain.model.Quote;

/**
 * 문구 요약 응답 DTO
 * 원본 문구와 요약된 문구 정보를 포함합니다.
 */
public record QuoteSummaryResponse(
    Long id,
    String originalContent,
    String summarizedContent,
    String bookTitle,
    String authorNickname
) {
    public static QuoteSummaryResponse from(Quote quote, String summarizedContent) {
        return new QuoteSummaryResponse(
            quote.getId(),
            quote.getContent(),
            summarizedContent,
            quote.getBook().getTitle(),
            quote.getUser().getNickname()
        );
    }
}