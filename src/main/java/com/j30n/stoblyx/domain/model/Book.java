package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseTimeEntity;
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
public class Book extends BaseTimeEntity {

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

    @ElementCollection
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    private boolean deleted = false;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Summary> summaries = new ArrayList<>();

    @Builder
    public Book(String title, String author, String isbn, String description, String publisher, LocalDate publishDate, List<String> genres) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.publisher = publisher;
        this.publishDate = publishDate;
        if (genres != null) {
            this.genres = genres;
        }
    }

    public void update(String title, String author, String isbn, String description, String publisher, LocalDate publishDate, List<String> genres) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.publisher = publisher;
        this.publishDate = publishDate;
        if (genres != null) {
            this.genres = genres;
        }
    }

    public void delete() {
        this.deleted = true;
    }
} 