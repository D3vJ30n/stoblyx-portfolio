package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 활동 정보를 저장하는 엔티티
 * 사용자의 좋아요, 저장, 댓글, 신고 등의 활동을 기록
 */
@Entity
@Table(name = "ranking_user_activity")
@Getter
@NoArgsConstructor
public class RankingUserActivity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Builder
    public RankingUserActivity(Long userId, ActivityType activityType, Integer points, 
                              LocalDateTime activityDate, Long referenceId, String referenceType) {
        this.userId = userId;
        this.activityType = activityType;
        this.points = points != null ? points : activityType.getScoreWeight();
        this.activityDate = activityDate != null ? activityDate : LocalDateTime.now();
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }

    @PrePersist
    protected void onCreate() {
        // 활동 유형에 따른 포인트 설정
        if (points == null) {
            points = activityType.getScoreWeight();
        }
        
        if (activityDate == null) {
            activityDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateModifiedAt();
    }
} 