package com.j30n.stoblyx.adapter.in.web.dto.book;

import com.j30n.stoblyx.domain.model.Book;
import java.time.LocalDateTime;

public record BookResponse(
    Long id,
    String title,
    String author,
    String isbn,
    String thumbnailUrl,
    String description,
    String publisher,
    Integer publicationYear,
    Integer totalPages,
    LocalDateTime createdAt
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            book.getThumbnailUrl(),
            book.getDescription(),
            book.getPublisher(),
            book.getPublicationYear(),
            book.getTotalPages(),
            book.getCreatedAt()
        );
    }
    
    // 빌더 패턴 추가
    public static BookResponseBuilder builder() {
        return new BookResponseBuilder();
    }
    
    public static class BookResponseBuilder {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private String thumbnailUrl;
        private String description;
        private String publisher;
        private Integer publicationYear;
        private Integer totalPages;
        private LocalDateTime createdAt;
        
        public BookResponseBuilder id(Long id) { this.id = id; return this; }
        public BookResponseBuilder title(String title) { this.title = title; return this; }
        public BookResponseBuilder author(String author) { this.author = author; return this; }
        public BookResponseBuilder isbn(String isbn) { this.isbn = isbn; return this; }
        public BookResponseBuilder thumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; return this; }
        public BookResponseBuilder description(String description) { this.description = description; return this; }
        public BookResponseBuilder publisher(String publisher) { this.publisher = publisher; return this; }
        public BookResponseBuilder publicationYear(Integer publicationYear) { this.publicationYear = publicationYear; return this; }
        public BookResponseBuilder totalPages(Integer totalPages) { this.totalPages = totalPages; return this; }
        public BookResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        
        public BookResponse build() {
            return new BookResponse(id, title, author, isbn, thumbnailUrl, description, publisher, publicationYear, totalPages, createdAt);
        }
    }
} 