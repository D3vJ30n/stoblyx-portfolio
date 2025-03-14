package com.j30n.stoblyx.application.service.ranking;

import com.j30n.stoblyx.adapter.in.web.dto.badge.RankingBadgeResponse;
import com.j30n.stoblyx.application.port.in.ranking.RankingBadgeUseCase;
import com.j30n.stoblyx.application.port.out.ranking.RankingAchievementPort;
import com.j30n.stoblyx.application.port.out.ranking.RankingBadgePort;
import com.j30n.stoblyx.application.port.out.user.UserPort;
import com.j30n.stoblyx.domain.model.RankingAchievement;
import com.j30n.stoblyx.domain.model.RankingBadge;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 배지 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RankingBadgeService implements RankingBadgeUseCase {

    private final RankingBadgePort rankingBadgePort;
    private final RankingAchievementPort rankingAchievementPort;
    private final UserPort userPort;

    /**
     * 모든 배지 목록을 조회합니다.
     *
     * @return 전체 배지 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingBadgeResponse> getAllBadges() {
        List<RankingBadge> badges = rankingBadgePort.findAll();
        return badges.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * 사용자가 획득한 배지 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 배지 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingBadgeResponse> getUserBadges(Long userId) {
        // 사용자 존재 여부 확인
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 사용자의 배지 획득 내역 조회
        List<RankingAchievement> achievements = rankingAchievementPort.findByUserId(userId);

        // 배지 정보와 함께 응답 DTO로 변환
        return achievements.stream()
            .map(this::mapToResponseWithAchievement)
            .collect(Collectors.toList());
    }

    /**
     * 특정 배지의 상세 정보를 조회합니다.
     *
     * @param badgeId 배지 ID
     * @return 배지 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 배지인 경우
     */
    @Override
    @Transactional(readOnly = true)
    public RankingBadgeResponse getBadgeDetail(Long badgeId) {
        RankingBadge badge = rankingBadgePort.findById(badgeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배지입니다."));

        return mapToResponse(badge);
    }

    /**
     * 사용자가 최근에 획득한 배지 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param limit  조회할 배지 수량
     * @return 최근 획득한 배지 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingBadgeResponse> getRecentBadges(Long userId, int limit) {
        // 사용자 존재 여부 확인
        User user = userPort.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 최근 획득한 배지 내역 조회 (최신순)
        List<RankingAchievement> recentAchievements = rankingAchievementPort.findByUserId(userId)
            .stream()
            .sorted((a1, a2) -> a2.getAchievedAt().compareTo(a1.getAchievedAt()))
            .limit(limit)
            .collect(Collectors.toList());

        // 배지 정보와 함께 응답 DTO로 변환
        return recentAchievements.stream()
            .map(this::mapToResponseWithAchievement)
            .collect(Collectors.toList());
    }

    /**
     * 특정 유형의 배지 목록을 조회합니다.
     *
     * @param badgeType 배지 유형
     * @return 해당 유형의 배지 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingBadgeResponse> getBadgesByType(String badgeType) {
        List<RankingBadge> badges = rankingBadgePort.findAll()
            .stream()
            .filter(badge -> badge.getBadgeType().equals(badgeType))
            .collect(Collectors.toList());

        return badges.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * 배지 엔티티를 응답 DTO로 변환합니다.
     *
     * @param badge 배지 엔티티
     * @return 배지 응답 DTO
     */
    private RankingBadgeResponse mapToResponse(RankingBadge badge) {
        return new RankingBadgeResponse(
            badge.getId(),
            badge.getBadgeType(),
            badge.getName(),
            badge.getDescription(),
            badge.getImageUrl(),
            badge.getRequirementType(),
            badge.getThresholdValue(),
            null,
            false
        );
    }

    /**
     * 배지 획득 내역을 포함한 응답 DTO로 변환합니다.
     *
     * @param achievement 배지 획득 엔티티
     * @return 배지 응답 DTO
     */
    private RankingBadgeResponse mapToResponseWithAchievement(RankingAchievement achievement) {
        RankingBadge badge = achievement.getBadge();
        return new RankingBadgeResponse(
            badge.getId(),
            badge.getBadgeType(),
            badge.getName(),
            badge.getDescription(),
            badge.getImageUrl(),
            badge.getRequirementType(),
            badge.getThresholdValue(),
            achievement.getAchievedAt(),
            true
        );
    }
} 