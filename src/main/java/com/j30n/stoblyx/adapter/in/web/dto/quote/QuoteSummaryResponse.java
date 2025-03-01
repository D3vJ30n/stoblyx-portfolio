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
    
    // 빌더 패턴 추가
    public static QuoteSummaryResponseBuilder builder() {
        return new QuoteSummaryResponseBuilder();
    }
    
    public static class QuoteSummaryResponseBuilder {
        private Long id;
        private String originalContent;
        private String summarizedContent;
        private String bookTitle;
        private String authorNickname;
        
        public QuoteSummaryResponseBuilder id(Long id) { this.id = id; return this; }
        public QuoteSummaryResponseBuilder originalContent(String originalContent) { this.originalContent = originalContent; return this; }
        public QuoteSummaryResponseBuilder summarizedContent(String summarizedContent) { this.summarizedContent = summarizedContent; return this; }
        public QuoteSummaryResponseBuilder bookTitle(String bookTitle) { this.bookTitle = bookTitle; return this; }
        public QuoteSummaryResponseBuilder authorNickname(String authorNickname) { this.authorNickname = authorNickname; return this; }
        
        public QuoteSummaryResponse build() {
            return new QuoteSummaryResponse(id, originalContent, summarizedContent, bookTitle, authorNickname);
        }
    }
}