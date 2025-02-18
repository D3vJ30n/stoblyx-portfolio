package com.j30n.stoblyx.domain.model.book;

import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.summary.Summary;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "books",
    indexes = {
        @Index(name = "idx_book_title", columnList = "title"),
        @Index(name = "idx_book_author", columnList = "author")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("책 고유 식별자")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    @Comment("책 제목")
    private String title;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    @Comment("저자")
    private String author;

    @Size(max = 100)
    @Column
    @Comment("책 장르")
    private String genre;

    @Column
    @Comment("출판일")
    private LocalDate publishedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private final List<Quote> quotes = new ArrayList<>();

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL)
    private Summary summary;

    // Builder pattern for immutable object creation
    @Builder
    private Book(String title, String author, String genre, LocalDate publishedAt) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publishedAt = publishedAt;
    }

    // Business methods
    public void addQuote(Quote quote) {
        this.quotes.add(quote);
        quote.setBook(this);
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
        summary.setBook(this);
    }
} 