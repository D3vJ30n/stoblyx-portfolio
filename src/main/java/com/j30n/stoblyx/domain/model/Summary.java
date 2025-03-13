package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 100)
    private String chapter;

    @Column(length = 50)
    private String page;

    @Builder
    public Summary(Book book, String content, String chapter, String page) {
        this.book = book;
        this.content = content;
        this.chapter = chapter;
        this.page = page;
    }

    public void update(String content, String chapter, String page) {
        this.content = content != null ? content : this.content;
        this.chapter = chapter != null ? chapter : this.chapter;
        this.page = page != null ? page : this.page;
        updateModifiedAt();
    }

    @Override
    public void delete() {
        super.delete();
        updateModifiedAt();
    }
} 