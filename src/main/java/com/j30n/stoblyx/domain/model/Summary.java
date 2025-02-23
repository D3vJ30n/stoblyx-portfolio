package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "summaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String content;

    private String chapter;

    @Column(nullable = false)
    private Integer startPage;

    @Column(nullable = false)
    private Integer endPage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Builder
    public Summary(String content, String chapter, Integer startPage, Integer endPage, Book book) {
        this.content = content;
        this.chapter = chapter;
        this.startPage = startPage;
        this.endPage = endPage;
        this.book = book;
    }

    public void update(String content, String chapter, Integer startPage, Integer endPage) {
        this.content = content;
        this.chapter = chapter;
        this.startPage = startPage;
        this.endPage = endPage;
    }
} 