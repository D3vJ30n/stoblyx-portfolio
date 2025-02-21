package com.j30n.stoblyx.domain.model.savedquote;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

/**
 * 저장된 인용구 엔티티
 * JPA 스펙을 만족하기 위해 protected 기본 생성자가 필요하며,
 * 외부에서 new 키워드를 통한 직접 생성을 막기 위해 protected로 선언
 */
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
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '저장된 인용구 고유 식별자'")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false, columnDefinition = "BIGINT COMMENT '저장된 인용구'")
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT COMMENT '인용구를 저장한 사용자'")
    private User user;

    /**
     * 저장된 인용구 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private SavedQuote(Quote quote, User user) {
        this.quote = Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    /**
     * 인용구를 저장하는 정적 팩토리 메서드
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public static SavedQuote createSavedQuote(Quote quote, User user) {
        SavedQuote savedQuote = SavedQuote.builder()
            .quote(quote)
            .user(user)
            .build();
        quote.getSavedQuotes().add(savedQuote);
        return savedQuote;
    }

    /**
     * 연관관계 편의 메서드 - 사용자 설정
     */
    public void setUser(User user) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedQuote that)) return false;
        return Objects.equals(id, that.id) &&
            Objects.equals(quote.getId(), that.quote.getId()) &&
            Objects.equals(user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quote.getId(), user.getId());
    }
} 