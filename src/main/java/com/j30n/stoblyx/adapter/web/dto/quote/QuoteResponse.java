package com.j30n.stoblyx.adapter.web.dto.quote;

import com.j30n.stoblyx.domain.model.Quote;

public record QuoteResponse(
    Long id,
    String content,
    Integer page,
    String chapter,
    Long userId,
    String username,
    Long bookId,
    String bookTitle,
    int likeCount,
    int saveCount
) {
    public static QuoteResponse from(Quote quote) {
        return new QuoteResponse(
            quote.getId(),
            quote.getContent(),
            quote.getPage(),
            quote.getChapter(),
            quote.getUser().getId(),
            quote.getUser().getUsername(),
            quote.getBook().getId(),
            quote.getBook().getTitle(),
            quote.getLikeCount(),
            quote.getSaveCount()
        );
    }
} 