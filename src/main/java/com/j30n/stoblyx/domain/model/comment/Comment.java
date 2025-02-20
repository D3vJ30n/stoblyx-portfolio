package com.j30n.stoblyx.domain.model.comment;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

/**
 * 댓글 엔티티
 * JPA 스펙을 만족하기 위해 protected 기본 생성자가 필요하며,
 * 외부에서 new 키워드를 통한 직접 생성을 막기 위해 protected로 선언
 */
@Entity
@Table(
    name = "comments",
    indexes = {
        @Index(name = "idx_comment_quote_id", columnList = "quote_id"),
        @Index(name = "idx_comment_user_id", columnList = "user_id")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private final List<Comment> replies = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("댓글 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @org.hibernate.annotations.Comment("댓글 작성자")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @org.hibernate.annotations.Comment("댓글이 달린 인용구")
    private Quote quote;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false)
    @org.hibernate.annotations.Comment("댓글 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @org.hibernate.annotations.Comment("부모 댓글 (대댓글인 경우)")
    private Comment parent;

    /**
     * 댓글 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private Comment(User user, Quote quote, String content, Comment parent) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        this.quote = Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        this.content = Objects.requireNonNull(content, "댓글 내용은 null일 수 없습니다");
        this.parent = parent;
    }

    /**
     * 인용구에 댓글을 생성하는 정적 팩토리 메서드
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public static Comment createComment(String content, Quote quote, User user) {
        Comment comment = Comment.builder()
            .content(content)
            .quote(quote)
            .user(user)
            .build();
        quote.getComments().add(comment);
        return comment;
    }

    /**
     * 댓글에 답글을 생성하는 정적 팩토리 메서드
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public Comment createReply(String content, User user) {
        Comment reply = Comment.builder()
            .content(content)
            .quote(this.quote)
            .user(user)
            .parent(this)
            .build();
        this.replies.add(reply);
        return reply;
    }

    // Business methods
    private void addReply(Comment reply) {
        Objects.requireNonNull(reply, "답글은 null일 수 없습니다");
        this.replies.add(reply);
    }

    /**
     * 답글 추가를 위한 내부 메서드
     */
    protected void addReplyInternal(Comment reply) {
        Objects.requireNonNull(reply, "답글은 null일 수 없습니다");
        this.replies.add(reply);
    }

    /**
     * 답글 목록 조회
     */
    public List<Comment> getReplies() {
        return Collections.unmodifiableList(replies);
    }

    // Update methods
    public void updateContent(String content) {
        this.content = Objects.requireNonNull(content, "댓글 내용은 null일 수 없습니다");
    }

    public void setUser(User user) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    public void setQuote(Quote quote) {
        this.quote = Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return Objects.equals(id, comment.id) &&
            Objects.equals(content, comment.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }
} 