package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.AchievementStatus;
import com.j30n.stoblyx.domain.model.RankingAchievement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     * 사용자 ID와 상태로 업적 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @param status 업적 상태
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByUserIdAndStatus(Long userId, AchievementStatus status);

    /**
     * 업적 코드로 업적 정보 조회
     *
     * @param achievementCode 업적 코드
     * @return 업적 정보
     */
    Optional<RankingAchievement> findByAchievementCode(String achievementCode);

    /**
     * 사용자 ID와 업적 코드로 업적 정보 조회
     *
     * @param userId          사용자 ID
     * @param achievementCode 업적 코드
     * @return 업적 정보
     */
    Optional<RankingAchievement> findByUserIdAndAchievementCode(Long userId, String achievementCode);

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
     * @return 업적 코드와 달성 횟수 목록
     */
    @Query("SELECT a.achievementCode, COUNT(a) FROM RankingAchievement a " +
           "WHERE a.status = 'COMPLETED' " +
           "GROUP BY a.achievementCode " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> findMostAchievedAchievements(Pageable pageable);

    /**
     * 업적 코드로 달성 횟수 조회
     *
     * @param achievementCode 업적 코드
     * @return 달성 횟수
     */
    Long countByAchievementCode(String achievementCode);

    /**
     * 사용자 ID와 상태로 업적 개수 조회
     *
     * @param userId 사용자 ID
     * @param status 업적 상태
     * @return 업적 개수
     */
    Long countByUserIdAndStatus(Long userId, AchievementStatus status);
} 