package com.j30n.stoblyx.domain.enums;

/**
 * 사용자 활동 유형을 정의하는 열거형
 * 각 활동 유형별로 점수 가중치가 다름
 */
public enum ActivityType {
    LIKE(2, "좋아요"),
    SAVE(3, "저장"),
    COMMENT(1, "댓글"),
    REPORT(-5, "신고"),
    ADMIN_ADJUSTMENT(0, "관리자 점수 조정"),
    ADMIN_SUSPENSION(0, "관리자 계정 정지"),
    ADMIN_UNSUSPENSION(0, "관리자 계정 정지 해제");

    private final int scoreWeight;
    private final String displayName;

    ActivityType(int scoreWeight, String displayName) {
        this.scoreWeight = scoreWeight;
        this.displayName = displayName;
    }

    public int getScoreWeight() {
        return scoreWeight;
    }

    public String getDisplayName() {
        return displayName;
    }
} 