package com.j30n.stoblyx.domain.model.ranking;

import com.j30n.stoblyx.domain.model.quote.Quote;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
    name = "rankings",
    indexes = {
        @Index(name = "idx_ranking_quote_id", columnList = "quote_id"),
        @Index(name = "idx_ranking_period", columnList = "period")
    }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Ranking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("랭킹 고유 식별자")
    private Long id;

    @Min(0)
    @Column(nullable = false)
    @Comment("좋아요 수")
    private Integer likeCount = 0;

    @Min(0)
    @Column(nullable = false)
    @Comment("저장 횟수")
    private Integer saveCount = 0;

    @Min(0)
    @Column(nullable = false)
    @Comment("댓글 수")
    private Integer commentCount = 0;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    @Comment("집계 기간 (weekly, monthly)")
    private String period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    @Comment("랭킹을 집계할 문구")
    private Quote quote;

    // Builder pattern for immutable object creation
    @Builder
    private Ranking(Integer likeCount, Integer saveCount, Integer commentCount, String period, Quote quote) {
        this.likeCount = likeCount;
        this.saveCount = saveCount;
        this.commentCount = commentCount;
        this.period = period;
        this.quote = quote;
    }

    // Business methods
    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementSaveCount() {
        this.saveCount++;
    }

    public void decrementSaveCount() {
        if (this.saveCount > 0) {
            this.saveCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public int calculateTotalScore() {
        return (likeCount * 2) + (saveCount * 3) + commentCount;
    }
} 