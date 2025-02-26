package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortFormContent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String audioUrl;

    private String bgmUrl;

    @Column(length = 1000)
    private String subtitles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.PROCESSING;

    private int viewCount = 0;
    private int likeCount = 0;
    private int shareCount = 0;

    private boolean deleted = false;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentInteraction> interactions = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentComment> comments = new ArrayList<>();

    @Builder
    public ShortFormContent(Book book, Quote quote, String videoUrl, String thumbnailUrl,
                          String imageUrl, String audioUrl, String bgmUrl, String subtitles,
                          ContentStatus status) {
        this.book = book;
        this.quote = quote;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.bgmUrl = bgmUrl;
        this.subtitles = subtitles;
        this.status = status != null ? status : ContentStatus.PROCESSING;
    }

    public void updateStatus(ContentStatus status) {
        this.status = status;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void updateLikeCount(int delta) {
        this.likeCount += delta;
    }

    public void incrementShareCount() {
        this.shareCount++;
    }

    public void delete() {
        this.deleted = true;
    }
}