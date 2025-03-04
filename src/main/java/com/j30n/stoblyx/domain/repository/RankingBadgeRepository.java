package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.BadgeRarity;
import com.j30n.stoblyx.domain.model.RankingBadge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 랭킹 뱃지 리포지토리 인터페이스
 */
@Repository
public interface RankingBadgeRepository extends JpaRepository<RankingBadge, Long> {

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
     * @param pageable 페이징 정보
     * @return 뱃지 코드와 획득 횟수 목록
     */
    @Query("SELECT b.badgeCode, COUNT(b) FROM RankingBadge b " +
           "GROUP BY b.badgeCode " +
           "ORDER BY COUNT(b) DESC")
    List<Object[]> findMostAcquiredBadges(Pageable pageable);

    /**
     * 뱃지 코드로 획득 횟수 조회
     *
     * @param badgeCode 뱃지 코드
     * @return 획득 횟수
     */
    Long countByBadgeCode(String badgeCode);

    /**
     * 사용자 ID로 뱃지 개수 조회
     *
     * @param userId 사용자 ID
     * @return 뱃지 개수
     */
    Long countByUserId(Long userId);
} 