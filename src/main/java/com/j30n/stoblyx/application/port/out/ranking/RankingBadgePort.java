package com.j30n.stoblyx.application.port.out.ranking;

import com.j30n.stoblyx.domain.model.RankingBadge;

import java.util.List;
import java.util.Optional;

/**
 * 랭킹 뱃지 관련 포트 인터페이스
 */
public interface RankingBadgePort {

    /**
     * 뱃지 정보 저장
     *
     * @param badge 뱃지 정보
     * @return 저장된 뱃지 정보
     */
    RankingBadge saveBadge(RankingBadge badge);

    /**
     * 뱃지 정보 목록 저장
     *
     * @param badges 뱃지 정보 목록
     * @return 저장된 뱃지 정보 목록
     */
    List<RankingBadge> saveAllBadges(List<RankingBadge> badges);

    /**
     * ID로 뱃지 정보 조회
     *
     * @param badgeId 뱃지 ID
     * @return 뱃지 정보
     */
    Optional<RankingBadge> findById(Long badgeId);

    /**
     * 가장 많이 획득된 뱃지 목록 조회
     *
     * @param limit 조회 개수
     * @return 뱃지 타입과 획득 횟수 목록
     */
    List<Object[]> findMostAcquiredBadges(int limit);

    /**
     * 뱃지 타입으로 획득 횟수 조회
     *
     * @param badgeType 뱃지 타입
     * @return 획득 횟수
     */
    Long countByBadgeType(String badgeType);
} 