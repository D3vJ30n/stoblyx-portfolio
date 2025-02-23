package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer page;

    private String chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToOne(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Video video;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<SavedQuotes> savedQuotes = new ArrayList<>();

    @Builder
    public Quote(String content, Integer page, String chapter, User user, Book book) {
        this.content = content;
        this.page = page;
        this.chapter = chapter;
        this.user = user;
        this.book = book;
    }

    public void update(String content, Integer page, String chapter) {
        this.content = content;
        this.page = page;
        this.chapter = chapter;
    }

    public void setVideo(Video video) {
        this.video = video;
        if (video != null && video.getQuote() != this) {
            video.setQuote(this);
        }
    }

    public int getLikeCount() {
        return this.likes.size();
    }

    public int getSaveCount() {
        return this.savedQuotes.size();
    }
} 