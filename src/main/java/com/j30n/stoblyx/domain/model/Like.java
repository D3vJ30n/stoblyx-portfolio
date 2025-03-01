package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(Like.LikeId.class)
public class Like extends BaseEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @Builder
    public Like(User user, Quote quote) {
        this.user = user;
        this.quote = quote;
    }

    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    /**
     * Like 엔티티의 복합키 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeId implements Serializable {
        private Long user;
        private Long quote;
    }
} 