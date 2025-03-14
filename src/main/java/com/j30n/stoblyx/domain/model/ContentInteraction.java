package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONTENT_INTERACTION")
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
    
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Builder
    public ContentInteraction(User user, ShortFormContent content) {
        this.user = user;
        this.content = content;
        this.viewedAt = LocalDateTime.now();
        this.isDeleted = false;
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
    
    public void delete() {
        this.isDeleted = true;
    }
    
    public void restore() {
        this.isDeleted = false;
    }

    // 테스트 데이터 초기화용 메서드
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
} 