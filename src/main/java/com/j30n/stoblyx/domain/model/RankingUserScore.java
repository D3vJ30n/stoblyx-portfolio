package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.RankType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 점수 정보를 저장하는 엔티티
 * 가중 이동 평균(EWMA) 알고리즘을 적용한 점수 계산 결과를 저장
 */
@Entity
@Table(name = "ranking_user_score")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingUserScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "current_score", nullable = false)
    private Integer currentScore;

    @Column(name = "previous_score")
    private Integer previousScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type", nullable = false)
    private RankType rankType;

    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "suspicious_activity", nullable = false)
    private Boolean suspiciousActivity;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount;

    @Column(name = "account_suspended", nullable = false)
    private Boolean accountSuspended;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
        lastActivityDate = LocalDateTime.now();

        // 기본값 설정
        if (currentScore == null) {
            currentScore = 1000; // 초기 점수
        }
        if (rankType == null) {
            rankType = RankType.fromScore(currentScore);
        }
        if (suspiciousActivity == null) {
            suspiciousActivity = false;
        }
        if (reportCount == null) {
            reportCount = 0;
        }
        if (accountSuspended == null) {
            accountSuspended = false;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();

        // 점수에 따른 랭크 업데이트
        rankType = RankType.fromScore(currentScore);
    }

    /**
     * 가중 이동 평균(EWMA) 알고리즘을 적용하여 점수 업데이트
     *
     * @param newActivityScore 새로운 활동 점수
     * @param alpha            가중치 (0.0 ~ 1.0)
     */
    public void updateScoreWithEWMA(int newActivityScore, double alpha) {
        if (alpha < 0.0 || alpha > 1.0) {
            throw new IllegalArgumentException("Alpha must be between 0.0 and 1.0");
        }

        previousScore = currentScore;
        currentScore = (int) Math.round(alpha * newActivityScore + (1 - alpha) * currentScore);
        rankType = RankType.fromScore(this.currentScore);
        lastActivityDate = LocalDateTime.now();
    }

    /**
     * 신고 횟수 증가 및 계정 정지 여부 확인
     *
     * @param suspensionThreshold 계정 정지 임계값
     * @return 계정 정지 여부
     */
    public boolean incrementReportCount(int suspensionThreshold) {
        reportCount++;

        // 신고 횟수가 임계값을 초과하면 계정 정지
        if (reportCount >= suspensionThreshold) {
            accountSuspended = true;
            // 점수 크게 감소
            currentScore = Math.max(0, currentScore - 100);
        }

        return accountSuspended;
    }

    /**
     * 비활동 기간에 따른 점수 감소
     *
     * @param decayFactor 감소 계수
     */
    public void decayScoreForInactivity(double decayFactor) {
        currentScore = (int) Math.round(currentScore * (1 - decayFactor));
    }
} 