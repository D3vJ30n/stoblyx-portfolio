package com.j30n.stoblyx.adapter.out.persistence.entity;

import com.j30n.stoblyx.domain.base.BaseTimeEntity;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.like.LikeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Objects;

/**
 * 좋아요 정보를 저장하는 JPA 엔티티
 * likes 테이블과 매핑됩니다.
 */
@Entity
@Table(
    name = "likes",
    indexes = {
        @Index(name = "idx_like_quote_id", columnList = "quote_id"),
        @Index(name = "idx_like_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_like_quote_user", columnNames = {"quote_id", "user_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class LikeJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("좋아요 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("좋아요를 누른 사용자")
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @Comment("좋아요가 달린 인용구")
    private QuoteJpaEntity quote;

    @Column(nullable = false)
    @Comment("활성화 여부")
    private boolean isActive;

    /**
     * 도메인 엔티티로부터 JPA 엔티티를 생성합니다.
     */
    public static LikeJpaEntity fromDomainEntity(Like like) {
        Objects.requireNonNull(like, "좋아요 도메인 엔티티는 null일 수 없습니다");

        LikeJpaEntity entity = new LikeJpaEntity();
        if (like.getId() != null) {
            entity.setId(like.getId().value());
        }
        entity.setActive(like.isActive());
        entity.setCreatedAt(like.getCreatedAt());
        entity.setModifiedAt(like.getModifiedAt());

        return entity;
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 변환합니다.
     */
    public Like toDomainEntity() {
        Like like = Like.create(
            this.user.toDomainEntity(),
            this.quote.toDomainEntity()
        );

        if (this.id != null) {
            like.setId(new LikeId(this.id));
        }
        if (!this.isActive) {
            like.cancel();
        }
        like.setTimeInfo(this.getCreatedAt(), this.getModifiedAt());

        return like;
    }
} 