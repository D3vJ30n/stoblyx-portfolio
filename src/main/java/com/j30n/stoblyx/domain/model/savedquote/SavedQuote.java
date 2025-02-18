package com.j30n.stoblyx.domain.model.savedquote;

import com.j30n.stoblyx.domain.model.quote.Quote;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
    name = "saved_quotes",
    indexes = {
        @Index(name = "idx_saved_quote_quote_id", columnList = "quote_id"),
        @Index(name = "idx_saved_quote_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_saved_quote_quote_user",
            columnNames = {"quote_id", "user_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SavedQuote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("저장된 문구 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @Comment("저장된 문구")
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("문구를 저장한 사용자")
    private User user;

    // Builder pattern for immutable object creation
    @Builder
    private SavedQuote(Quote quote, User user) {
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