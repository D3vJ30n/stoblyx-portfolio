package com.j30n.stoblyx.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 책의 기본 정보를 담는 값 객체
 */
@Getter
@Builder
public class BookInfo {
    private final String title;
    private final String author;
    private final String isbn;
    private final String description;
    private final String publisher;
    private final LocalDate publishDate;
    private final String thumbnailUrl;
    private final List<String> genres;
    private final Integer publicationYear;
    private final Integer totalPages;
    
    // 알라딘 API 원본 표지 이미지 URL
    private final String cover;
    
    // 알라딘 API 관련 필드
    private final String itemId;           // 알라딘 상품 ID
    private final String isbn13;           // ISBN13
    private final Integer priceStandard;   // 정가
    private final Integer priceSales;      // 판매가
    private final String categoryId;       // 카테고리 ID
    private final String categoryName;     // 카테고리명
    private final String link;             // 상품 링크
    private final String adult;            // 성인여부
    private final Float customerReviewRank; // 고객 평점
    private final String stockStatus;      // 재고상태
    private final String mallType;         // 상품 몰 타입 (BOOK, MUSIC, DVD 등)
} 