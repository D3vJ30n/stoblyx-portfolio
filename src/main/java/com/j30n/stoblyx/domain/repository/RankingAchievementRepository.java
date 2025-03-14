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
 * 랭킹 업적 관련 리포지토리
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
     * 사용자 ID와 배지 ID로 업적 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @param badgeId 배지 ID
     * @return 존재 여부
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM RankingAchievement a " +
           "WHERE a.user.id = :userId AND a.badge.id = :badgeId")
    boolean existsByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    /**
     * 사용자 ID와 배지 ID로 업적 정보 조회
     *
     * @param userId 사용자 ID
     * @param badgeId 배지 ID
     * @return 업적 정보
     */
    @Query("SELECT a FROM RankingAchievement a WHERE a.user.id = :userId AND a.badge.id = :badgeId")
    Optional<RankingAchievement> findByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    /**
     * 배지 ID로 획득한 사용자 수 조회
     *
     * @param badgeId 배지 ID
     * @return 획득한 사용자 수
     */
    @Query("SELECT COUNT(a) FROM RankingAchievement a WHERE a.badge.id = :badgeId")
    Long countByBadgeId(@Param("badgeId") Long badgeId);

    /**
     * 배지 유형으로 업적 정보 조회
     *
     * @param badgeType 배지 유형
     * @return 업적 정보
     */
    @Query("SELECT a FROM RankingAchievement a WHERE a.badge.badgeType = :badgeType")
    Optional<RankingAchievement> findByBadgeType(@Param("badgeType") String badgeType);

    /**
     * 사용자 ID와 배지 유형으로 업적 정보 조회
     *
     * @param userId 사용자 ID
     * @param badgeType 배지 유형
     * @return 업적 정보
     */
    @Query("SELECT a FROM RankingAchievement a WHERE a.user.id = :userId AND a.badge.badgeType = :badgeType")
    Optional<RankingAchievement> findByUserIdAndBadgeType(@Param("userId") Long userId, @Param("badgeType") String badgeType);

    /**
     * 특정 기간 내에 획득한 업적 정보 목록 조회
     *
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByAchievedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 가장 많이 획득한 업적 정보 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 업적 정보 목록
     */
    @Query("SELECT a.badge.badgeType, COUNT(a) FROM RankingAchievement a " +
           "GROUP BY a.badge.badgeType " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> findMostAchievedAchievements(Pageable pageable);

    /**
     * 특정 배지 유형을 획득한 사용자 수 조회
     *
     * @param badgeType 배지 유형
     * @return 사용자 수
     */
    @Query("SELECT COUNT(a) FROM RankingAchievement a WHERE a.badge.badgeType = :badgeType")
    Long countByBadgeType(@Param("badgeType") String badgeType);
} 