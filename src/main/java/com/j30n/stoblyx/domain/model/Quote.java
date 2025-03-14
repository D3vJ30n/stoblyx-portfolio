package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 책에서 사용자가 발췌한 인용구를 저장하는 엔티티
 */
@Entity
@Table(name = "quotes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "FK_QUOTE_BOOK"))
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

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private final List<Comment> comments = new ArrayList<>();
    
    @OneToOne(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private QuoteSummary summary;

    @Builder
    public Quote(User user, Book book, String content, String memo, Integer page) {
        this.user = user;
        this.book = book;
        this.content = content;
        this.memo = memo;
        this.page = page;
    }

    /**
     * 인용구 내용을 업데이트합니다.
     */
    public void update(String content, String memo, Integer page) {
        this.content = content;
        this.memo = memo;
        this.page = page;
    }

    /**
     * 좋아요 수를 업데이트합니다.
     */
    public void updateLikeCount(Integer delta) {
        this.likeCount += delta;
    }

    /**
     * 저장 수를 업데이트합니다.
     */
    public void updateSaveCount(Integer delta) {
        this.saveCount += delta;
    }

    /**
     * 좋아요 수를 증가시킵니다.
     */
    public void incrementLikeCount() {
        this.likeCount = this.likeCount + 1;
    }

    /**
     * 좋아요 수를 감소시킵니다.
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount = this.likeCount - 1;
        }
    }

    /**
     * 저장 수를 증가시킵니다.
     */
    public void incrementSaveCount() {
        this.saveCount = this.saveCount + 1;
    }

    /**
     * 저장 수를 감소시킵니다.
     */
    public void decrementSaveCount() {
        if (this.saveCount > 0) {
            this.saveCount = this.saveCount - 1;
        }
    }

    /**
     * 메모를 업데이트합니다.
     */
    public void updateMemo(String memo) {
        this.memo = memo;
    }
} 