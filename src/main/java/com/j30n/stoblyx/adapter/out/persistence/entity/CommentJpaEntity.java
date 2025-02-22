package com.j30n.stoblyx.adapter.out.persistence.entity;

import com.j30n.stoblyx.domain.base.BaseTimeEntity;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.model.comment.CommentId;
import com.j30n.stoblyx.domain.model.comment.Content;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 댓글 정보를 저장하는 JPA 엔티티
 * comments 테이블과 매핑됩니다.
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
@Setter
@NoArgsConstructor
public class CommentJpaEntity extends BaseTimeEntity {

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private final List<CommentJpaEntity> replies = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("댓글 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @org.hibernate.annotations.Comment("댓글 작성자")
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @org.hibernate.annotations.Comment("댓글이 달린 인용구")
    private QuoteJpaEntity quote;

    @Column(nullable = false)
    @org.hibernate.annotations.Comment("댓글 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @org.hibernate.annotations.Comment("부모 댓글 (대댓글인 경우)")
    private CommentJpaEntity parent;

    @Column(name = "book_id", nullable = false)
    @org.hibernate.annotations.Comment("책 ID")
    private Long bookId;

    @Column(nullable = false)
    @org.hibernate.annotations.Comment("삭제 여부")
    private boolean isDeleted;

    /**
     * 도메인 엔티티로부터 JPA 엔티티를 생성합니다.
     */
    public static CommentJpaEntity fromDomainEntity(Comment comment) {
        Objects.requireNonNull(comment, "댓글 도메인 엔티티는 null일 수 없습니다");

        CommentJpaEntity entity = new CommentJpaEntity();
        if (comment.getId() != null) {
            entity.setId(comment.getId().value());
        }
        entity.setContent(comment.getContent().value());
        entity.setBookId(comment.getBookId().value());
        entity.setDeleted(comment.isDeleted());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setModifiedAt(comment.getModifiedAt());

        return entity;
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     */
    public Comment toDomainEntity() {
        Comment comment = Comment.createComment(
            new Content(this.content),
            this.quote.toDomainEntity(),
            this.user.toDomainEntity(),
            new BookId(this.bookId)
        );

        if (this.id != null) {
            comment.setId(new CommentId(this.id));
        }
        comment.setTimeInfo(this.getCreatedAt(), this.getModifiedAt());

        return comment;
    }
} 