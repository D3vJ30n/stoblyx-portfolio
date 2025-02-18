package com.j30n.stoblyx.domain.model.quote;

import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.savedquote.SavedQuote;
import com.j30n.stoblyx.domain.model.video.Video;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("문구 고유 식별자")
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("문구 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @Comment("문구가 속한 책")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("문구를 등록한 사용자")
    private User user;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private final List<SavedQuote> savedQuotes = new ArrayList<>();

    @OneToOne(mappedBy = "quote", cascade = CascadeType.ALL)
    private Video video;

    // Builder pattern for immutable object creation
    @Builder
    private Quote(String content, Book book, User user) {
        this.content = content;
        this.book = book;
        this.user = user;
    }

    // Business methods
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setQuote(this);
    }

    public void addLike(Like like) {
        this.likes.add(like);
        like.setQuote(this);
    }

    public void addSavedQuote(SavedQuote savedQuote) {
        this.savedQuotes.add(savedQuote);
        savedQuote.setQuote(this);
    }

    public void setVideo(Video video) {
        this.video = video;
        video.setQuote(this);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBook(Book book) {
        this.book = book;
    }
} 