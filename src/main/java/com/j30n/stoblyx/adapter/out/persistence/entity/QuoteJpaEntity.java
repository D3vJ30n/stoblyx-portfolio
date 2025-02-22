package com.j30n.stoblyx.adapter.out.persistence.entity;

import com.j30n.stoblyx.domain.base.BaseTimeEntity;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Content;
import com.j30n.stoblyx.domain.model.quote.Page;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
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
 * 인용구 정보를 저장하는 JPA 엔티티
 * quotes 테이블과 매핑됩니다.
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
@Setter
@NoArgsConstructor
public class QuoteJpaEntity extends BaseTimeEntity {

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private final List<CommentJpaEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private final List<LikeJpaEntity> likes = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("인용구 고유 식별자")
    private Long id;

    @Column(nullable = false, length = 5000)
    @Comment("인용구 내용")
    private String content;

    @Column(name = "book_id", nullable = false)
    @Comment("책 ID")
    private Long bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("인용구를 등록한 사용자")
    private UserJpaEntity user;

    @Column(nullable = false)
    @Comment("페이지 번호")
    private int page;

    @Column(nullable = false)
    @Comment("삭제 여부")
    private boolean isDeleted;

    /**
     * 도메인 엔티티로부터 JPA 엔티티를 생성합니다.
     */
    public static QuoteJpaEntity fromDomainEntity(Quote quote) {
        Objects.requireNonNull(quote, "인용구 도메인 엔티티는 null일 수 없습니다");

        QuoteJpaEntity entity = new QuoteJpaEntity();
        if (quote.getId() != null) {
            entity.setId(quote.getId().value());
        }
        entity.setContent(quote.getContent().value());
        entity.setBookId(quote.getBookId().value());
        entity.setPage(quote.getPage().value());
        entity.setDeleted(quote.isDeleted());
        entity.setCreatedAt(quote.getCreatedAt());
        entity.setModifiedAt(quote.getModifiedAt());

        return entity;
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     */
    public Quote toDomainEntity() {
        Quote quote = Quote.builder()
            .content(new Content(this.content))
            .bookId(new BookId(this.bookId))
            .page(new Page(this.page))
            .user(this.user.toDomainEntity())
            .build();

        if (this.id != null) {
            quote.setId(new QuoteId(this.id));
        }
        if (this.isDeleted) {
            quote.delete();
        }
        quote.setTimeInfo(this.getCreatedAt(), this.getModifiedAt());

        return quote;
    }
} 