package com.j30n.stoblyx.domain.model.like;

import com.j30n.stoblyx.domain.model.quote.Quote;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
    name = "likes",
    indexes = {
        @Index(name = "idx_like_quote_id", columnList = "quote_id"),
        @Index(name = "idx_like_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_like_quote_user",
            columnNames = {"quote_id", "user_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("좋아요 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @Comment("좋아요가 달린 문구")
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("좋아요를 누른 사용자")
    private User user;

    // Builder pattern for immutable object creation
    @Builder
    private Like(Quote quote, User user) {
        this.quote = quote;
        this.user = user;
    }

    // Business methods
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public void setUser(User user) {
        this.user = user;
    }
} 