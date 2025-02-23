package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String videoUrl;

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    private String style;

    private String bgmType;

    private Integer duration;

    @Builder
    public Video(String videoUrl, String thumbnailUrl, String style, String bgmType, Quote quote) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.style = style;
        this.bgmType = bgmType;
        this.setQuote(quote);
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
        if (quote != null && quote.getVideo() != this) {
            quote.setVideo(this);
        }
    }

    public void updateStatus(VideoStatus status) {
        this.status = status;
    }

    public void updateUrls(String videoUrl, String thumbnailUrl) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
} 