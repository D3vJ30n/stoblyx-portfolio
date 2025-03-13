package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ShortFormContent shortFormContent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public ContentComment(User user, ShortFormContent shortFormContent, String content) {
        this.user = user;
        this.shortFormContent = shortFormContent;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}