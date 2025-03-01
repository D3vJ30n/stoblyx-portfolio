package com.j30n.stoblyx.adapter.in.web.dto.book;

import com.j30n.stoblyx.domain.model.Book;
import java.time.LocalDateTime;
import java.util.List;

public record BookDetailResponse(
    Long id,
    String title,
    String author,
    String isbn,
    String description,
    String publisher,
    Integer publicationYear,
    String coverImage,
    List<String> categories,
    Integer totalPages,
    Integer avgReadingTime,
    Double averageRating,
    Integer ratingCount,
    LocalDateTime createdAt
) {
    public static BookDetailResponse from(Book book) {
        return BookDetailResponse.builder()
            .id(book.getId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .isbn(book.getIsbn())
            .description(book.getDescription())
            .publisher(book.getPublisher())
            .publicationYear(book.getPublicationYear())
            .coverImage(book.getThumbnailUrl())
            .categories(book.getGenres())
            .totalPages(book.getTotalPages())
            .avgReadingTime(book.getAvgReadingTime())
            .averageRating(book.getAverageRating())
            .ratingCount(book.getRatingCount())
            .createdAt(book.getCreatedAt())
            .build();
    }
    
    // 빌더 패턴 추가
    public static BookDetailResponseBuilder builder() {
        return new BookDetailResponseBuilder();
    }
    
    public static class BookDetailResponseBuilder {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private String description;
        private String publisher;
        private Integer publicationYear;
        private String coverImage;
        private List<String> categories;
        private Integer totalPages;
        private Integer avgReadingTime;
        private Double averageRating;
        private Integer ratingCount;
        private LocalDateTime createdAt;
        
        public BookDetailResponseBuilder id(Long id) { this.id = id; return this; }
        public BookDetailResponseBuilder title(String title) { this.title = title; return this; }
        public BookDetailResponseBuilder author(String author) { this.author = author; return this; }
        public BookDetailResponseBuilder isbn(String isbn) { this.isbn = isbn; return this; }
        public BookDetailResponseBuilder description(String description) { this.description = description; return this; }
        public BookDetailResponseBuilder publisher(String publisher) { this.publisher = publisher; return this; }
        public BookDetailResponseBuilder publicationYear(Integer publicationYear) { this.publicationYear = publicationYear; return this; }
        public BookDetailResponseBuilder coverImage(String coverImage) { this.coverImage = coverImage; return this; }
        public BookDetailResponseBuilder categories(List<String> categories) { this.categories = categories; return this; }
        public BookDetailResponseBuilder totalPages(Integer totalPages) { this.totalPages = totalPages; return this; }
        public BookDetailResponseBuilder avgReadingTime(Integer avgReadingTime) { this.avgReadingTime = avgReadingTime; return this; }
        public BookDetailResponseBuilder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public BookDetailResponseBuilder ratingCount(Integer ratingCount) { this.ratingCount = ratingCount; return this; }
        public BookDetailResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        
        public BookDetailResponse build() {
            return new BookDetailResponse(id, title, author, isbn, description, publisher, publicationYear, coverImage, 
                    categories, totalPages, avgReadingTime, averageRating, ratingCount, createdAt);
        }
    }
} 