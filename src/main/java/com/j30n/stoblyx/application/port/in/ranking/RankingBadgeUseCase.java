package com.j30n.stoblyx.application.port.in.ranking;

import com.j30n.stoblyx.adapter.in.web.dto.badge.RankingBadgeResponse;

import java.util.List;

/**
 * 배지(Badge) 관련 비즈니스 로직을 처리하는 인터페이스
 */
public interface RankingBadgeUseCase {

    /**
     * 모든 배지 목록을 조회합니다.
     *
     * @return 전체 배지 목록
     */
    List<RankingBadgeResponse> getAllBadges();

    /**
     * 사용자가 획득한 배지 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 배지 목록
     */
    List<RankingBadgeResponse> getUserBadges(Long userId);

    /**
     * 특정 배지의 상세 정보를 조회합니다.
     *
     * @param badgeId 배지 ID
     * @return 배지 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 배지인 경우
     */
    RankingBadgeResponse getBadgeDetail(Long badgeId);

    /**
     * 사용자가 최근에 획득한 배지 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param limit  조회할 배지 수량
     * @return 최근 획득한 배지 목록
     */
    List<RankingBadgeResponse> getRecentBadges(Long userId, int limit);

    /**
     * 특정 유형의 배지 목록을 조회합니다.
     *
     * @param badgeType 배지 유형
     * @return 해당 유형의 배지 목록
     */
    List<RankingBadgeResponse> getBadgesByType(String badgeType);
} 