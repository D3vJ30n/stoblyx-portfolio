package com.j30n.stoblyx.application.port.out.ranking;

import com.j30n.stoblyx.domain.enums.BadgeRarity;
import com.j30n.stoblyx.domain.model.RankingBadge;

import java.time.LocalDateTime;
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
     * 사용자 ID로 뱃지 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @return 뱃지 정보 목록
     */
    List<RankingBadge> findByUserId(Long userId);

    /**
     * 뱃지 코드로 뱃지 정보 조회
     *
     * @param badgeCode 뱃지 코드
     * @return 뱃지 정보
     */
    Optional<RankingBadge> findByBadgeCode(String badgeCode);

    /**
     * 사용자 ID와 뱃지 코드로 뱃지 정보 조회
     *
     * @param userId    사용자 ID
     * @param badgeCode 뱃지 코드
     * @return 뱃지 정보
     */
    Optional<RankingBadge> findByUserIdAndBadgeCode(Long userId, String badgeCode);

    /**
     * 희귀도로 뱃지 정보 목록 조회
     *
     * @param rarity 희귀도
     * @return 뱃지 정보 목록
     */
    List<RankingBadge> findByRarity(BadgeRarity rarity);

    /**
     * 획득 일시 범위로 뱃지 정보 목록 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 뱃지 정보 목록
     */
    List<RankingBadge> findByAcquiredAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 가장 많이 획득된 뱃지 목록 조회
     *
     * @param limit 조회 개수
     * @return 뱃지 코드와 획득 횟수 목록
     */
    List<Object[]> findMostAcquiredBadges(int limit);

    /**
     * 뱃지 코드로 획득 횟수 조회
     *
     * @param badgeCode 뱃지 코드
     * @return 획득 횟수
     */
    Long countByBadgeCode(String badgeCode);

    /**
     * 사용자의 뱃지 획득률 계산
     *
     * @param userId      사용자 ID
     * @param totalBadges 전체 뱃지 수
     * @return 획득률 (%)
     */
    Double calculateUserBadgeRate(Long userId, int totalBadges);
} 