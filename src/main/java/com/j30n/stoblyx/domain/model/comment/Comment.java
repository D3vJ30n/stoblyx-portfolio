package com.j30n.stoblyx.domain.model.comment;

import com.j30n.stoblyx.domain.model.quote.Quote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("댓글 고유 식별자")
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("댓글 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @Comment("댓글이 달린 문구")
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("댓글을 작성한 사용자")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment("부모 댓글 (대댓글인 경우)")
    private Comment parent;

    // Builder pattern for immutable object creation
    @Builder
    private Comment(String content, Quote quote, User user, Comment parent) {
        this.content = content;
        this.quote = quote;
        this.user = user;
        this.parent = parent;
    }

    // Business methods
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }
} 