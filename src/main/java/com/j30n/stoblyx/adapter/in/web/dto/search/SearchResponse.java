package com.j30n.stoblyx.adapter.in.web.dto.search;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 검색 결과를 위한 DTO
 */
@Getter
@Builder
public class SearchResponse {
    private Long id;
    private String type;  // "QUOTE" or "BOOK"
    private String title;
    private String content;
    private String author;
    private String category;  // 첫 번째 장르를 카테고리로 사용
    private LocalDateTime createdAt;

    public static SearchResponse fromQuote(Quote quote) {
        Book book = quote.getBook();
        return SearchResponse.builder()
            .id(quote.getId())
            .type("QUOTE")
            .title(book.getTitle())
            .content(quote.getContent())
            .author(book.getAuthor())
            .category(book.getGenres().isEmpty() ? "기타" : book.getGenres().get(0))  // 첫 번째 장르를 카테고리로 사용
            .createdAt(quote.getCreatedAt())
            .build();
    }

    public static SearchResponse fromBook(Book book) {
        return SearchResponse.builder()
            .id(book.getId())
            .type("BOOK")
            .title(book.getTitle())
            .content(book.getDescription())
            .author(book.getAuthor())
            .category(book.getGenres().isEmpty() ? "기타" : book.getGenres().get(0))  // 첫 번째 장르를 카테고리로 사용
            .createdAt(book.getCreatedAt())
            .build();
    }
}
