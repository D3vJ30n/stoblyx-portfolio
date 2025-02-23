package com.j30n.stoblyx.adapter.web.dto.book;

import com.j30n.stoblyx.domain.model.Book;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookResponse(
    Long id,
    String title,
    String author,
    String isbn,
    String description,
    String publisher,
    LocalDate publishDate,
    List<String> genres,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    @Builder
    public BookResponse {}

    public static BookResponse from(Book book) {
        return BookResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .isbn(book.getIsbn())
            .description(book.getDescription())
            .publisher(book.getPublisher())
            .publishDate(book.getPublishDate())
            .genres(book.getGenres())
            .createdAt(book.getCreatedAt())
            .modifiedAt(book.getModifiedAt())
            .build();
    }
} 