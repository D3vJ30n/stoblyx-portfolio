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
@Table(name = "books")
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
    
    // 알라딘 API의 원본 표지 이미지 URL
    private String cover;

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();
    private Integer publicationYear;
    private Integer totalPages;
    private Integer avgReadingTime = 0;
    private Double averageRating = 0.0;
    private Integer ratingCount = 0;

    /**
     * 책의 인기도 점수
     * 조회수, 좋아요, 댓글 등을 기반으로 계산됨
     */
    private Integer popularity = 0;
    
    /**
     * 알라딘 API 관련 필드
     */
    private String itemId;           // 알라딘 상품 ID
    private String isbn13;           // ISBN13
    private Integer priceStandard;   // 정가
    private Integer priceSales;      // 판매가
    private String categoryId;       // 카테고리 ID
    private String categoryName;     // 카테고리명
    private String link;             // 상품 링크
    private String adult;            // 성인여부
    private Float customerReviewRank; // 고객 평점
    private String stockStatus;      // 재고 상태
    private String mallType;         // 상품 몰 타입

    @Builder
    public Book(BookInfo bookInfo) {
        this.title = bookInfo.getTitle();
        this.author = bookInfo.getAuthor();
        this.isbn = bookInfo.getIsbn();
        this.description = bookInfo.getDescription();
        this.publisher = bookInfo.getPublisher();
        this.publishDate = bookInfo.getPublishDate();
        this.thumbnailUrl = bookInfo.getThumbnailUrl();
        this.cover = bookInfo.getCover();
        this.publicationYear = bookInfo.getPublicationYear();
        this.totalPages = bookInfo.getTotalPages();
        this.popularity = 0;
        
        // 알라딘 API 관련 필드 초기화
        this.itemId = bookInfo.getItemId();
        this.isbn13 = bookInfo.getIsbn13();
        this.priceStandard = bookInfo.getPriceStandard();
        this.priceSales = bookInfo.getPriceSales();
        this.categoryId = bookInfo.getCategoryId();
        this.categoryName = bookInfo.getCategoryName();
        this.link = bookInfo.getLink();
        this.adult = bookInfo.getAdult();
        this.customerReviewRank = bookInfo.getCustomerReviewRank();
        this.stockStatus = bookInfo.getStockStatus();
        this.mallType = bookInfo.getMallType();
        
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
        this.cover = bookInfo.getCover();
        
        // 알라딘 API 관련 필드 업데이트
        this.itemId = bookInfo.getItemId();
        this.isbn13 = bookInfo.getIsbn13();
        this.priceStandard = bookInfo.getPriceStandard();
        this.priceSales = bookInfo.getPriceSales();
        this.categoryId = bookInfo.getCategoryId();
        this.categoryName = bookInfo.getCategoryName();
        this.link = bookInfo.getLink();
        this.adult = bookInfo.getAdult();
        this.customerReviewRank = bookInfo.getCustomerReviewRank();
        this.stockStatus = bookInfo.getStockStatus();
        this.mallType = bookInfo.getMallType();
        
        if (bookInfo.getPublicationYear() != null) {
            this.publicationYear = bookInfo.getPublicationYear();
        }
        
        if (bookInfo.getTotalPages() != null) {
            this.totalPages = bookInfo.getTotalPages();
        }
        
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

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }
    
    // 알라딘 API 관련 필드 getter
    public String getItemId() {
        return itemId;
    }
    
    public String getIsbn13() {
        return isbn13;
    }
    
    public Integer getPriceStandard() {
        return priceStandard;
    }
    
    public Integer getPriceSales() {
        return priceSales;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public String getLink() {
        return link;
    }
    
    public String getAdult() {
        return adult;
    }
    
    public Float getCustomerReviewRank() {
        return customerReviewRank;
    }
    
    public String getStockStatus() {
        return stockStatus;
    }
    
    public String getMallType() {
        return mallType;
    }
    
    public String getCover() {
        return cover;
    }
} 