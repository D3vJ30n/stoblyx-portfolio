package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.ActivityType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 사용자 활동 정보를 저장하는 엔티티
 * 사용자의 좋아요, 저장, 댓글, 신고 등의 활동을 기록
 */
@Entity
@Table(name = "ranking_user_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingUserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "score_change", nullable = false)
    private Integer scoreChange;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // 활동 유형에 따른 점수 변화 설정
        if (scoreChange == null) {
            scoreChange = activityType.getScoreWeight();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 