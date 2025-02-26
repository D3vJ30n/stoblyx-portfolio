package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentInteraction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ShortFormContent content;

    private boolean liked;
    private boolean bookmarked;
    private LocalDateTime viewedAt;

    @Builder
    public ContentInteraction(User user, ShortFormContent content) {
        this.user = user;
        this.content = content;
        this.viewedAt = LocalDateTime.now();
    }

    public void toggleLike() {
        this.liked = !this.liked;
        this.content.updateLikeCount(this.liked ? 1 : -1);
    }

    public void toggleBookmark() {
        this.bookmarked = !this.bookmarked;
    }

    public void updateViewedAt() {
        this.viewedAt = LocalDateTime.now();
        this.content.incrementViewCount();
    }
} 