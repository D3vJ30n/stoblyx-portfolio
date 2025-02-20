package com.j30n.stoblyx.domain.model.summary;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.book.Book;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
    name = "summaries",
    indexes = {
        @Index(name = "idx_summary_book_id", columnList = "book_id")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '요약 고유 식별자'")
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT COMMENT '요약 내용'", nullable = false)
    private String summaryText;

    @Min(0)
    @Column(columnDefinition = "INT COMMENT '원본 텍스트 길이'", nullable = false)
    private Integer originalLength;

    @Min(0)
    @Column(columnDefinition = "INT COMMENT '요약된 텍스트 길이'", nullable = false)
    private Integer summaryLength;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, unique = true, columnDefinition = "BIGINT COMMENT '요약된 책'")
    private Book book;

    /**
     * 요약 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private Summary(String summaryText, Integer originalLength, Integer summaryLength, Book book) {
        this.summaryText = Objects.requireNonNull(summaryText, "요약 내용은 null일 수 없습니다");
        this.originalLength = Objects.requireNonNull(originalLength, "원본 텍스트 길이는 null일 수 없습니다");
        this.summaryLength = Objects.requireNonNull(summaryLength, "요약된 텍스트 길이는 null일 수 없습니다");
        setBook(book);  // 연관관계 설정은 setBook 메서드를 통해 처리
    }

    // Business methods
    /**
     * 연관관계 편의 메서드 - 책 설정
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public void setBook(Book book) {
        Objects.requireNonNull(book, "책은 null일 수 없습니다");

        // 기존 책이 있다면 연관관계 제거
        if (this.book != null && this.book != book) {
            this.book.removeSummary();
        }

        this.book = book;
        book.setSummary(this);
    }

    /**
     * 책 연관관계 제거
     */
    public void removeBook() {
        if (this.book != null) {
            Book oldBook = this.book;
            this.book = null;
            oldBook.removeSummary();
        }
    }

    /**
     * 요약 내용 업데이트
     */
    public void updateSummary(String summaryText, Integer originalLength, Integer summaryLength) {
        this.summaryText = Objects.requireNonNull(summaryText, "요약 내용은 null일 수 없습니다");
        this.originalLength = Objects.requireNonNull(originalLength, "원본 텍스트 길이는 null일 수 없습니다");
        this.summaryLength = Objects.requireNonNull(summaryLength, "요약된 텍스트 길이는 null일 수 없습니다");
    }
} 