package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ShortFormContent content;

    @Column(nullable = false, length = 500)
    private String commentText;

    private int likeCount = 0;
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ContentComment parent;

    @Builder
    public ContentComment(User user, ShortFormContent content, String commentText, ContentComment parent) {
        this.user = user;
        this.content = content;
        this.commentText = commentText;
        this.parent = parent;
    }

    public void updateContent(String commentText) {
        this.commentText = commentText;
    }

    public void updateLikeCount(int delta) {
        this.likeCount += delta;
    }

    public void delete() {
        this.deleted = true;
    }
} 