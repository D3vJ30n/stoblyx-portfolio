package com.j30n.stoblyx.adapter.in.web.dto.search;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;

import java.time.LocalDateTime;

/**
 * 검색 결과를 위한 DTO
 */
public record SearchResponse(
    Long id,
    String type,  // "QUOTE" or "BOOK"
    String title,
    String content,
    String author,
    String category,  // 첫 번째 장르를 카테고리로 사용
    LocalDateTime createdAt
) {
    public static SearchResponse fromQuote(Quote quote) {
        Book book = quote.getBook();
        return builder()
            .id(quote.getId())
            .type("QUOTE")
            .title(book.getTitle())
            .content(quote.getContent())
            .author(book.getAuthor())
            .category(book.getGenres().isEmpty() ? "기타" : book.getGenres().get(0))
            .createdAt(quote.getCreatedAt())
            .build();
    }

    public static SearchResponse fromBook(Book book) {
        return builder()
            .id(book.getId())
            .type("BOOK")
            .title(book.getTitle())
            .content(book.getDescription())
            .author(book.getAuthor())
            .category(book.getGenres().isEmpty() ? "기타" : book.getGenres().get(0))
            .createdAt(book.getCreatedAt())
            .build();
    }

    // 빌더 패턴 추가
    public static SearchResponseBuilder builder() {
        return new SearchResponseBuilder();
    }

    public static class SearchResponseBuilder {
        private Long id;
        private String type;
        private String title;
        private String content;
        private String author;
        private String category;
        private LocalDateTime createdAt;

        public SearchResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SearchResponseBuilder type(String type) {
            this.type = type;
            return this;
        }

        public SearchResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public SearchResponseBuilder content(String content) {
            this.content = content;
            return this;
        }

        public SearchResponseBuilder author(String author) {
            this.author = author;
            return this;
        }

        public SearchResponseBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SearchResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SearchResponse build() {
            return new SearchResponse(id, type, title, content, author, category, createdAt);
        }
    }
}
