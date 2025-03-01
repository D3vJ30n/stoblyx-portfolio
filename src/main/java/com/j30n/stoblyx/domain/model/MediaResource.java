package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 콘텐츠 관련 미디어 리소스(이미지, 오디오, 비디오 등)를 관리하는 엔티티
 */
@Entity
@Table(name = "media_resources")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediaResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    @Column(nullable = false)
    private String url;

    @Column
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ShortFormContent content;

    @Column(length = 1000)
    private String description;

    @Column
    private Integer duration;

    @Builder
    public MediaResource(MediaType type, String url, String thumbnailUrl, ShortFormContent content, 
                         String description, Integer duration) {
        this.type = type;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.content = content;
        this.description = description;
        this.duration = duration;
    }

    public void updateUrl(String url) {
        this.url = url;
    }

    public void updateThumbnail(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    /**
     * 미디어 리소스 타입을 정의하는 열거형
     */
    public enum MediaType {
        VIDEO,
        AUDIO,
        IMAGE,
        SUBTITLE,
        BGM
    }
} 