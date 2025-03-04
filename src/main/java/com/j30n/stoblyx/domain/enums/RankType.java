package com.j30n.stoblyx.domain.enums;

/**
 * 사용자 랭크 타입을 정의하는 열거형
 * 점수 범위에 따라 브론즈부터 다이아까지 등급이 나뉨
 */
public enum RankType {
    BRONZE(0, 1200, "브론즈"),
    SILVER(1201, 1500, "실버"),
    GOLD(1501, 1800, "골드"),
    PLATINUM(1801, 2100, "플래티넘"),
    DIAMOND(2101, Integer.MAX_VALUE, "다이아");

    private final int minScore;
    private final int maxScore;
    private final String displayName;

    RankType(int minScore, int maxScore, String displayName) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.displayName = displayName;
    }

    /**
     * 주어진 점수에 해당하는 랭크 타입을 반환
     * 
     * @param score 사용자 점수
     * @return 해당 점수의 랭크 타입
     */
    public static RankType fromScore(int score) {
        for (RankType rankType : values()) {
            if (score >= rankType.minScore && score <= rankType.maxScore) {
                return rankType;
            }
        }
        return BRONZE; // 기본값
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getDisplayName() {
        return displayName;
    }
} 