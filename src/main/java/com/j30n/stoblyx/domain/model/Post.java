package com.j30n.stoblyx.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Post(String title, String content, User author, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void undelete() {
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }
}
