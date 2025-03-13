package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.RankingAchievement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 랭킹 업적 리포지토리 인터페이스
 */
@Repository
public interface RankingAchievementRepository extends JpaRepository<RankingAchievement, Long> {

    /**
     * 사용자 ID로 업적 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByUserId(Long userId);

    /**
     * 사용자 ID로 업적 개수 조회
     *
     * @param userId 사용자 ID
     * @return 업적 개수
     */
    Long countByUserId(Long userId);

    /**
     * 뱃지 타입으로 업적 정보 조회
     *
     * @param badgeType 뱃지 타입
     * @return 업적 정보
     */
    @Query("SELECT a FROM RankingAchievement a WHERE a.badge.badgeType = :badgeType")
    Optional<RankingAchievement> findByBadgeType(@Param("badgeType") String badgeType);

    /**
     * 사용자 ID와 뱃지 타입으로 업적 정보 조회
     *
     * @param userId    사용자 ID
     * @param badgeType 뱃지 타입
     * @return 업적 정보
     */
    @Query("SELECT a FROM RankingAchievement a WHERE a.user.id = :userId AND a.badge.badgeType = :badgeType")
    Optional<RankingAchievement> findByUserIdAndBadgeType(@Param("userId") Long userId, @Param("badgeType") String badgeType);

    /**
     * 달성 일시 범위로 업적 정보 목록 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByAchievedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 가장 많이 달성된 업적 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 뱃지 타입과 달성 횟수 목록
     */
    @Query("SELECT a.badge.badgeType, COUNT(a) FROM RankingAchievement a " +
           "GROUP BY a.badge.badgeType " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> findMostAchievedAchievements(Pageable pageable);

    /**
     * 뱃지 타입으로 달성 횟수 조회
     *
     * @param badgeType 뱃지 타입
     * @return 달성 횟수
     */
    @Query("SELECT COUNT(a) FROM RankingAchievement a WHERE a.badge.badgeType = :badgeType")
    Long countByBadgeType(@Param("badgeType") String badgeType);
} 