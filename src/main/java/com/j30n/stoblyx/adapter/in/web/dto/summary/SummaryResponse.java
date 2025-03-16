package com.j30n.stoblyx.adapter.in.web.dto.summary;

import com.j30n.stoblyx.domain.model.Summary;

import java.time.LocalDateTime;

public record SummaryResponse(
    Long id,
    Long bookId,
    String content,
    String chapter,
    String page,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public static SummaryResponse from(Summary summary) {
        return new SummaryResponse(
            summary.getId(),
            summary.getBook().getId(),
            summary.getContent(),
            summary.getChapter(),
            summary.getPage(),
            summary.getCreatedAt(),
            summary.getModifiedAt()
        );
    }

    // 빌더 패턴 추가
    public static SummaryResponseBuilder builder() {
        return new SummaryResponseBuilder();
    }

    public static class SummaryResponseBuilder {
        private Long id;
        private Long bookId;
        private String content;
        private String chapter;
        private String page;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public SummaryResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SummaryResponseBuilder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        public SummaryResponseBuilder content(String content) {
            this.content = content;
            return this;
        }

        public SummaryResponseBuilder chapter(String chapter) {
            this.chapter = chapter;
            return this;
        }

        public SummaryResponseBuilder page(String page) {
            this.page = page;
            return this;
        }

        public SummaryResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SummaryResponseBuilder modifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public SummaryResponse build() {
            return new SummaryResponse(id, bookId, content, chapter, page, createdAt, modifiedAt);
        }
    }
} 