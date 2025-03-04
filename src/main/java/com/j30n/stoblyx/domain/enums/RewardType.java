package com.j30n.stoblyx.domain.enums;

/**
 * 게이미피케이션 보상 유형을 정의하는 열거형
 * 각 랭크별로 다른 보상이 제공됨
 */
public enum RewardType {
    BONUS_POINTS("보너스 포인트", "실버 이상 등급 도달 시 보너스 포인트 지급"),
    WEEKLY_EXPERIENCE("주간 경험치", "골드 등급 이상 사용자는 매주 추가 경험치 제공"),
    EVENT_INVITATION("이벤트 초대권", "플래티넘 이상 사용자는 커뮤니티 이벤트 초대권 제공"),
    ADMIN_RECOMMENDATION("관리자 추천", "다이아 등급 이상 사용자는 관리자 추천 피드에 노출");

    private final String displayName;
    private final String description;

    RewardType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
} 