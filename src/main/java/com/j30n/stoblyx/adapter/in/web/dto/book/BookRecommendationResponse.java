package com.j30n.stoblyx.adapter.in.web.dto.book;

import com.j30n.stoblyx.domain.model.Book;

import java.time.LocalDate;

/**
 * 도서 추천 정보를 응답하기 위한 DTO
 */
public record BookRecommendationResponse(
    Long id,
    String title,
    String author,
    String description,
    String thumbnailUrl,
    String publisher,
    Double similarityScore,
    String recommendationReason,
    LocalDate publishDate
) {
    /**
     * 도서 추천 응답 DTO의 컴팩트 생성자
     */
    public BookRecommendationResponse {
        // ID가 null이면 예외 발생
        if (id == null) {
            throw new IllegalArgumentException("도서 ID는 null일 수 없습니다.");
        }
        
        // 제목이 null이거나 빈 문자열이면 예외 발생
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("도서 제목은 null이거나 빈 문자열일 수 없습니다.");
        }
        
        // 저자가 null이거나 빈 문자열이면 예외 발생
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("도서 저자는 null이거나 빈 문자열일 수 없습니다.");
        }
    }
    
    /**
     * Book 엔티티와 추가 정보로부터 BookRecommendationResponse DTO를 생성합니다.
     *
     * @param book 도서 엔티티
     * @param similarityScore 유사도 점수
     * @param recommendationReason 추천 이유
     * @return BookRecommendationResponse DTO
     */
    public static BookRecommendationResponse fromEntity(Book book, Double similarityScore, String recommendationReason) {
        return new BookRecommendationResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getDescription(),
            book.getThumbnailUrl(),
            book.getPublisher(),
            similarityScore,
            recommendationReason,
            book.getPublishDate()
        );
    }
} 