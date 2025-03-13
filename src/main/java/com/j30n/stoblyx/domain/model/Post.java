package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Builder
    public Post(String title, String content, User user, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void update(String title, String content, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
    }
}
