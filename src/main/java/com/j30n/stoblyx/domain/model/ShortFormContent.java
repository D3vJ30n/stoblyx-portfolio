package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.enums.ContentType;
import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 책의 인용구를 기반으로 생성된 숏폼 콘텐츠를 관리하는 엔티티
 */
@Entity
@Table(name = "SHORT_FORM_CONTENTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortFormContent extends BaseEntity {

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MediaResource> mediaResources = new ArrayList<>();
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ContentInteraction> interactions = new ArrayList<>();
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ContentComment> comments = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(length = 1000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.PROCESSING;
    @Column
    private boolean deleted = false;
    @Column
    private int duration;
    private int viewCount = 0;
    private int likeCount = 0;
    private int shareCount = 0;
    private int commentCount = 0;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(columnDefinition = "TEXT")
    private String subtitles;

    @Column(columnDefinition = "TEXT")
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String audioUrl;

    @Builder
    public ShortFormContent(Book book, Quote quote, String title, String description, ContentStatus status) {
        this.book = book;
        this.quote = quote;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : ContentStatus.PROCESSING;
    }

    /**
     * 미디어 리소스를 콘텐츠에 추가합니다.
     */
    public void addMediaResource(MediaResource mediaResource) {
        this.mediaResources.add(mediaResource);
    }

    /**
     * 콘텐츠 상태를 업데이트합니다.
     */
    public void updateStatus(ContentStatus status) {
        this.status = status;
    }

    /**
     * 조회수를 증가시킵니다.
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 좋아요 수를 변경합니다.
     */
    public void updateLikeCount(int delta) {
        this.likeCount += delta;
    }

    /**
     * 공유 수를 증가시킵니다.
     */
    public void incrementShareCount() {
        this.shareCount++;
    }

    /**
     * 콘텐츠 제목을 업데이트합니다.
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * 콘텐츠 설명을 업데이트합니다.
     */
    public void updateDescription(String description) {
        this.description = description;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
    
    public void setSubtitles(String subtitles) {
        this.subtitles = subtitles;
    }
}