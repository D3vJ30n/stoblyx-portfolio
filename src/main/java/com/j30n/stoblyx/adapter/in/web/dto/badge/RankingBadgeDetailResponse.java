package com.j30n.stoblyx.adapter.in.web.dto.badge;

import com.j30n.stoblyx.domain.model.RankingBadge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 배지 상세 정보를 포함하는 응답 DTO
 */
@Getter
@Builder
public class RankingBadgeDetailResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final String badgeType;
    private final String requirementType;
    private final Integer thresholdValue;
    private final Integer pointsAwarded;
    private final LocalDateTime createdAt;
    private final String acquisitionCondition;
    private final Long achievedCount;
    private final String rarity;

    /**
     * RankingBadge 엔티티와 추가 정보로부터 BadgeDetailResponse DTO를 생성합니다.
     *
     * @param badge         배지 엔티티
     * @param achievedCount 획득한 사용자 수
     * @param totalUsers    전체 사용자 수
     * @return BadgeDetailResponse DTO
     */
    public static RankingBadgeDetailResponse fromEntity(RankingBadge badge, Long achievedCount, Long totalUsers) {
        String acquisitionCondition = generateAcquisitionCondition(badge);
        String rarity = calculateRarity(achievedCount, totalUsers);

        return RankingBadgeDetailResponse.builder()
            .id(badge.getId())
            .name(badge.getName())
            .description(badge.getDescription())
            .imageUrl(badge.getImageUrl())
            .badgeType(badge.getBadgeType())
            .requirementType(badge.getRequirementType())
            .thresholdValue(badge.getThresholdValue())
            .pointsAwarded(badge.getPointsAwarded())
            .createdAt(badge.getCreatedAt())
            .acquisitionCondition(acquisitionCondition)
            .achievedCount(achievedCount)
            .rarity(rarity)
            .build();
    }

    /**
     * 배지 획득 조건 문자열을 생성합니다.
     *
     * @param badge 배지 엔티티
     * @return 획득 조건 문자열
     */
    private static String generateAcquisitionCondition(RankingBadge badge) {
        String requirementType = badge.getRequirementType();
        Integer thresholdValue = badge.getThresholdValue();

        switch (requirementType) {
            case "CONTENT_CREATION":
                return String.format("%d개의 콘텐츠를 생성하세요", thresholdValue);
            case "CONTENT_VIEWS":
                return String.format("작성한 콘텐츠가 총 %d회 조회되어야 합니다", thresholdValue);
            case "LIKES_RECEIVED":
                return String.format("작성한 콘텐츠가 총 %d개의 좋아요를 받아야 합니다", thresholdValue);
            case "COMMENTS_RECEIVED":
                return String.format("작성한 콘텐츠에 총 %d개의 댓글을 받아야 합니다", thresholdValue);
            case "LOGIN_DAYS":
                return String.format("연속 %d일 로그인하세요", thresholdValue);
            case "BOOKS_READ":
                return String.format("%d권의 도서를 읽으세요", thresholdValue);
            default:
                return String.format("%s 조건에서 %d값을 달성하세요", requirementType, thresholdValue);
        }
    }

    /**
     * 배지 희귀도를 계산합니다.
     *
     * @param achievedCount 획득한 사용자 수
     * @param totalUsers    전체 사용자 수
     * @return 희귀도 문자열 (Common, Uncommon, Rare, Epic, Legendary)
     */
    private static String calculateRarity(Long achievedCount, Long totalUsers) {
        if (totalUsers == 0) return "Common";

        double percentage = ((double) achievedCount / totalUsers) * 100;

        if (percentage <= 1) return "Legendary";
        if (percentage <= 5) return "Epic";
        if (percentage <= 15) return "Rare";
        if (percentage <= 40) return "Uncommon";
        return "Common";
    }
} 