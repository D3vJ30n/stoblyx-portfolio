package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "likes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_likes", columnNames = {"user_id", "quote_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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
} 