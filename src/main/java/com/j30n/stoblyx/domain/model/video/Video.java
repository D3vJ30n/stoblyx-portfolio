package com.j30n.stoblyx.domain.model.video;

import com.j30n.stoblyx.domain.base.BaseEntity;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

/**
 * 동영상 엔티티
 * JPA 스펙을 만족하기 위해 protected 기본 생성자가 필요하며,
 * 외부에서 new 키워드를 통한 직접 생성을 막기 위해 protected로 선언
 */
@Entity
@Table(
    name = "videos",
    indexes = {
        @Index(name = "idx_video_quote_id", columnList = "quote_id"),
        @Index(name = "idx_video_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_video_quote",
            columnNames = "quote_id"
        )
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("동영상 고유 식별자")
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @Comment("동영상 URL")
    private String url;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false, unique = true)
    @Comment("동영상이 연결된 인용구")
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("동영상을 등록한 사용자")
    private User user;

    /**
     * 동영상 객체 생성을 위한 빌더 패턴
     * 생성 후 불변성을 보장하기 위해 private으로 선언
     */
    @Builder
    private Video(String url, Quote quote, User user) {
        this.url = Objects.requireNonNull(url, "동영상 URL은 null일 수 없습니다");
        this.quote = Objects.requireNonNull(quote, "인용구는 null일 수 없습니다");
        this.user = Objects.requireNonNull(user, "사용자는 null일 수 없습니다");
    }

    /**
     * 인용구에 동영상을 생성하는 정적 팩토리 메서드
     * 양방향 연관관계 설정을 자동으로 처리
     */
    public static Video createVideo(String url, Quote quote, User user) {
        Video video = Video.builder()
            .url(url)
            .quote(quote)
            .user(user)
            .build();
        quote.setVideo(video);
        return video;
    }

    // Update methods
    public void updateUrl(String url) {
        this.url = Objects.requireNonNull(url, "동영상 URL은 null일 수 없습니다");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Video video)) return false;
        return Objects.equals(id, video.id) &&
            Objects.equals(url, video.url) &&
            Objects.equals(quote.getId(), video.quote.getId()) &&
            Objects.equals(user.getId(), video.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, quote.getId(), user.getId());
    }
} 