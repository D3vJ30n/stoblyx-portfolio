package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseTimeEntity;
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
public class Quote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(nullable = false)
    private Integer page;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "save_count")
    private Integer saveCount = 0;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<SavedQuotes> savedQuotes = new ArrayList<>();

    @Builder
    public Quote(User user, Book book, String content, String memo, Integer page) {
        this.user = user;
        this.book = book;
        this.content = content;
        this.memo = memo;
        this.page = page;
    }

    public void update(String content, String memo, Integer page) {
        this.content = content;
        this.memo = memo;
        this.page = page;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementSaveCount() {
        this.saveCount++;
    }

    public void decrementSaveCount() {
        if (this.saveCount > 0) {
            this.saveCount--;
        }
    }
} 