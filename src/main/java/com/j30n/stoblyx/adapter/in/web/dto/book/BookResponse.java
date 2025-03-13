package com.j30n.stoblyx.adapter.in.web.dto.book;

import com.j30n.stoblyx.domain.model.Book;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public record BookResponse(
    Long id,
    String title,
    String author,
    String isbn,
    String thumbnailUrl,
    String description,
    String publisher,
    LocalDate publishDate,
    List<String> genres,
    Integer publicationYear,
    Integer totalPages,
    Integer avgReadingTime,
    Double averageRating,
    Integer ratingCount,
    Integer popularity,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    Boolean deleted
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
            book.getPublishDate(),
            book.getGenres() != null ? new ArrayList<>(book.getGenres()) : new ArrayList<>(),
            book.getPublicationYear(),
            book.getTotalPages(),
            book.getAvgReadingTime(),
            book.getAverageRating(),
            book.getRatingCount(),
            book.getPopularity(),
            book.getCreatedAt(),
            book.getModifiedAt(),
            book.isDeleted()
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
        private LocalDate publishDate;
        private List<String> genres = new ArrayList<>();
        private Integer publicationYear;
        private Integer totalPages;
        private Integer avgReadingTime;
        private Double averageRating;
        private Integer ratingCount;
        private Integer popularity;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private Boolean deleted;
        
        public BookResponseBuilder id(Long id) { this.id = id; return this; }
        public BookResponseBuilder title(String title) { this.title = title; return this; }
        public BookResponseBuilder author(String author) { this.author = author; return this; }
        public BookResponseBuilder isbn(String isbn) { this.isbn = isbn; return this; }
        public BookResponseBuilder thumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; return this; }
        public BookResponseBuilder description(String description) { this.description = description; return this; }
        public BookResponseBuilder publisher(String publisher) { this.publisher = publisher; return this; }
        public BookResponseBuilder publishDate(LocalDate publishDate) { this.publishDate = publishDate; return this; }
        public BookResponseBuilder genres(List<String> genres) { this.genres = genres; return this; }
        public BookResponseBuilder publicationYear(Integer publicationYear) { this.publicationYear = publicationYear; return this; }
        public BookResponseBuilder totalPages(Integer totalPages) { this.totalPages = totalPages; return this; }
        public BookResponseBuilder avgReadingTime(Integer avgReadingTime) { this.avgReadingTime = avgReadingTime; return this; }
        public BookResponseBuilder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public BookResponseBuilder ratingCount(Integer ratingCount) { this.ratingCount = ratingCount; return this; }
        public BookResponseBuilder popularity(Integer popularity) { this.popularity = popularity; return this; }
        public BookResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public BookResponseBuilder modifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; return this; }
        public BookResponseBuilder deleted(Boolean deleted) { this.deleted = deleted; return this; }
        
        public BookResponse build() {
            return new BookResponse(id, title, author, isbn, thumbnailUrl, description, publisher, publishDate, genres, publicationYear, totalPages, avgReadingTime, averageRating, ratingCount, popularity, createdAt, modifiedAt, deleted);
        }
    }
} 