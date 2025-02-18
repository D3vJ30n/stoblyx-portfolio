package com.j30n.stoblyx.domain.model.video;

import com.j30n.stoblyx.domain.model.quote.Quote;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
    name = "videos",
    indexes = {
        @Index(name = "idx_video_quote_id", columnList = "quote_id")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("영상 고유 식별자")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    @Comment("영상 URL")
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    @Comment("영상 설명")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false, unique = true)
    @Comment("연관된 문구")
    private Quote quote;

    // Builder pattern for immutable object creation
    @Builder
    private Video(String videoUrl, String description, Quote quote) {
        this.videoUrl = videoUrl;
        this.description = description;
        this.quote = quote;
    }

    // Business methods
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
} 