package com.j30n.stoblyx.domain.model.like;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

/**
 * 좋아요 엔티티
 * JPA 스펙을 만족하기 위해 protected 기본 생성자가 필요하며,
 * 외부에서 new 키워드를 통한 직접 생성을 막기 위해 protected로 선언
 */
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

    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    /**
     * 좋아요 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private Like(Quote quote, User user) {
        this.quote = Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 인용구에 좋아요를 생성하는 정적 팩토리 메서드
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public static Like createLike(Quote quote, User user) {
        Like like = Like.builder()
            .quote(quote)
            .user(user)
            .build();
        quote.getLikes().add(like);
        return like;
    }

    /**
     * 연관관계 편의 메서드 - 사용자 설정
     */
    public void setUser(User user) {
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    /**
     * ID 설정을 위한 메서드
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 시간 정보 설정을 위한 메서드
     */
    public void setTimeInfo(LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "생성 시간은 null일 수 없습니다");
        this.modifiedAt = Objects.requireNonNull(modifiedAt, "수정 시간은 null일 수 없습니다");
    }

    /**
     * 좋아요를 생성하는 정적 팩토리 메서드
     */
    public static Like create(User user, Quote quote) {
        Like like = new Like(user, quote);
        quote.getLikes().add(like);
        return like;
    }

    /**
     * 좋아요 취소
     */
    public void cancel() {
        this.isActive = false;
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 좋아요 활성화
     */
    public void activate() {
        this.isActive = true;
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * 작성자 확인
     */
    public boolean isAuthor(User user) {
        return this.user.equals(user);
    }

    /**
     * 활성화 여부 확인
     */
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like like)) return false;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 