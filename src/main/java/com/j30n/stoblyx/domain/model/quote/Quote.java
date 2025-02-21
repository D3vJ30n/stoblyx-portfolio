package com.j30n.stoblyx.domain.model.quote;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.book.Book;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.savedquote.SavedQuote;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.model.video.Video;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
 * 인용구 엔티티
 * JPA 스펙을 만족하기 위해 protected 기본 생성자가 필요하며,
 * 외부에서 new 키워드를 통한 직접 생성을 막기 위해 protected로 선언
 */
@Entity
@Table(
    name = "quotes",
    indexes = {
        @Index(name = "idx_quote_book_id", columnList = "book_id"),
        @Index(name = "idx_quote_user_id", columnList = "user_id")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Quote extends BaseEntity {

    @OneToMany(mappedBy = "quote", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @BatchSize(size = 100)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @BatchSize(size = 100)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @BatchSize(size = 100)
    private final List<SavedQuote> savedQuotes = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("문구 고유 식별자")
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @org.hibernate.annotations.Comment("문구 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @org.hibernate.annotations.Comment("문구가 속한 책")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @org.hibernate.annotations.Comment("문구를 등록한 사용자")
    private User user;

    @OneToOne(mappedBy = "quote", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Video video;

    /**
     * 인용구 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private Quote(String content, Book book, User user) {
        this.content = Objects.requireNonNull(content, "인용구 내용은 null일 수 없습니다");
        this.book = Objects.requireNonNull(book, "책은 null일 수 없습니다");
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    /**
     * 인용구 생성을 위한 정적 팩토리 메서드
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public static Quote createQuote(String content, Book book, User user) {
        Quote quote = Quote.builder()
            .content(content)
            .book(book)
            .user(user)
            .build();
        book.getQuotes().add(quote);
        user.getQuotes().add(quote);
        return quote;
    }

    // Business methods

    /**
     * 연관관계 편의 메서드 - 댓글 추가
     */
    public Comment addComment(String content, User user) {
        Objects.requireNonNull(content, "댓글 내용은 null일 수 없습니다");
        Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        Comment comment = Comment.builder()
            .content(content)
            .quote(this)
            .user(user)
            .build();
        this.comments.add(comment);
        user.getComments().add(comment);
        return comment;
    }

    /**
     * 연관관계 편의 메서드 - 답글 추가
     */
    public Comment addReply(String content, User user, Comment parent) {
        Objects.requireNonNull(content, "댓글 내용은 null일 수 없습니다");
        Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        Objects.requireNonNull(parent, "부모 댓글은 null일 수 없습니다");
        if (!this.comments.contains(parent)) {
            throw new IllegalArgumentException("부모 댓글이 현재 인용구에 속하지 않습니다");
        }
        Comment reply = Comment.builder()
            .content(content)
            .quote(this)
            .user(user)
            .parent(parent)
            .build();
        this.comments.add(reply);
        user.getComments().add(reply);
        parent.getReplies().add(reply);
        return reply;
    }

    /**
     * 연관관계 편의 메서드 - 좋아요 추가
     */
    public Like addLike(User user) {
        Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        Like like = Like.builder()
            .quote(this)
            .user(user)
            .build();
        this.likes.add(like);
        user.getLikes().add(like);
        return like;
    }

    /**
     * 연관관계 편의 메서드 - 저장된 인용구 추가
     */
    public SavedQuote addSavedQuote(User user) {
        Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        SavedQuote savedQuote = SavedQuote.builder()
            .quote(this)
            .user(user)
            .build();
        this.savedQuotes.add(savedQuote);
        user.getSavedQuotes().add(savedQuote);
        return savedQuote;
    }

    /**
     * 연관관계 편의 메서드 - 동영상 추가
     * 하나의 인용구에는 하나의 동영상만 연결될 수 있음
     */
    public Video addVideo(String url, User user) {
        Objects.requireNonNull(url, "동영상 URL은 null일 수 없습니다");
        Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        if (this.video != null) {
            throw new IllegalStateException("이미 동영상이 등록되어 있습니다");
        }
        Video video = Video.builder()
            .url(url)
            .quote(this)
            .user(user)
            .build();
        this.video = video;
        return video;
    }

    /**
     * 연관관계 편의 메서드 - 컬렉션 조회
     */
    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public List<Like> getLikes() {
        return Collections.unmodifiableList(likes);
    }

    public List<SavedQuote> getSavedQuotes() {
        return Collections.unmodifiableList(savedQuotes);
    }

    // Update methods
    public void updateContent(String content) {
        this.content = Objects.requireNonNull(content, "인용구 내용은 null일 수 없습니다");
    }

    public void setUser(User user) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    public void setBook(Book book) {
        this.book = Objects.requireNonNull(book, "책은 null일 수 없습니다");
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote quote)) return false;
        return Objects.equals(id, quote.id) &&
            Objects.equals(content, quote.content) &&
            Objects.equals(book.getId(), quote.book.getId()) &&
            Objects.equals(user.getId(), quote.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, book.getId(), user.getId());
    }
} 