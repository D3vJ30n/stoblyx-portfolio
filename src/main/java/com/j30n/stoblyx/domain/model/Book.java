package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private final List<Quote> quotes = new ArrayList<>();
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private final List<Summary> summaries = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(unique = true)
    private String isbn;
    @Column(length = 2000)
    private String description;
    private String publisher;
    private LocalDate publishDate;
    private String thumbnailUrl;
    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();
    private Integer publicationYear;
    private Integer totalPages;
    private Integer avgReadingTime;
    private Double averageRating;
    private Integer ratingCount;

    @Builder
    public Book(BookInfo bookInfo) {
        this.title = bookInfo.getTitle();
        this.author = bookInfo.getAuthor();
        this.isbn = bookInfo.getIsbn();
        this.description = bookInfo.getDescription();
        this.publisher = bookInfo.getPublisher();
        this.publishDate = bookInfo.getPublishDate();
        this.thumbnailUrl = bookInfo.getThumbnailUrl();
        if (bookInfo.getGenres() != null) {
            this.genres = new ArrayList<>(bookInfo.getGenres());
        }
    }

    /**
     * 도서 정보를 업데이트합니다
     */
    public void update(BookInfo bookInfo) {
        this.title = bookInfo.getTitle();
        this.author = bookInfo.getAuthor();
        this.isbn = bookInfo.getIsbn();
        this.description = bookInfo.getDescription();
        this.publisher = bookInfo.getPublisher();
        this.publishDate = bookInfo.getPublishDate();
        this.thumbnailUrl = bookInfo.getThumbnailUrl();
        if (bookInfo.getGenres() != null) {
            this.genres = new ArrayList<>(bookInfo.getGenres());
        }
    }

    /**
     * 도서를 논리적으로 삭제합니다
     * BaseEntity의 delete() 메서드를 호출하고 추가적인 처리를 수행합니다.
     */
    @Override
    public void delete() {
        super.delete();
        updateModifiedAt();
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getAvgReadingTime() {
        return avgReadingTime;
    }

    public void setAvgReadingTime(Integer avgReadingTime) {
        this.avgReadingTime = avgReadingTime;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
} 