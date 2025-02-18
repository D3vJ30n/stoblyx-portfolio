package com.j30n.stoblyx.domain.model.summary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

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
    @Comment("요약 고유 식별자")
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("요약 내용")
    private String summaryText;

    @Min(0)
    @Column(nullable = false)
    @Comment("원본 텍스트 길이")
    private Integer originalLength;

    @Min(0)
    @Column(nullable = false)
    @Comment("요약된 텍스트 길이")
    private Integer summaryLength;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    @Comment("요약된 책")
    private Book book;

    // Builder pattern for immutable object creation
    @Builder
    private Summary(String summaryText, Integer originalLength, Integer summaryLength, Book book) {
        this.summaryText = summaryText;
        this.originalLength = originalLength;
        this.summaryLength = summaryLength;
        this.book = book;
    }

    // Business methods
    public void setBook(Book book) {
        this.book = book;
    }

    public void updateSummary(String summaryText, Integer originalLength, Integer summaryLength) {
        this.summaryText = summaryText;
        this.originalLength = originalLength;
        this.summaryLength = summaryLength;
    }
} 