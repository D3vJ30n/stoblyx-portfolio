package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 활동 정보 레포지토리
 */
@Repository
public interface RankingUserActivityRepository extends JpaRepository<RankingUserActivity, Long> {

    /**
     * 사용자 ID로 활동 내역 조회
     * 
     * @param userId 사용자 ID
     * @return 활동 내역 목록
     */
    List<RankingUserActivity> findByUserId(Long userId);

    /**
     * 사용자 ID와 활동 유형으로 활동 내역 조회
     * 
     * @param userId 사용자 ID
     * @param activityType 활동 유형
     * @return 활동 내역 목록
     */
    List<RankingUserActivity> findByUserIdAndActivityType(Long userId, ActivityType activityType);

    /**
     * 특정 기간 내 사용자 활동 내역 조회
     * 
     * @param userId 사용자 ID
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 내역 목록
     */
    @Query("SELECT a FROM RankingUserActivity a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<RankingUserActivity> findByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 대상에 대한 활동 내역 조회
     * 
     * @param targetId 대상 ID
     * @param targetType 대상 유형
     * @return 활동 내역 목록
     */
    List<RankingUserActivity> findByReferenceIdAndReferenceType(Long targetId, String targetType);
    
    /**
     * 특정 기간 내 모든 활동 내역 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 내역 목록
     */
    @Query("SELECT a FROM RankingUserActivity a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<RankingUserActivity> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 기간 내에 점수가 급격히 증가한 의심스러운 활동을 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param threshold 점수 증가 임계값
     * @return 의심스러운 활동 목록 [userId, totalScoreChange, activityCount, lastActivityTime]
     */
    @Query("SELECT a.userId, SUM(a.points) as totalScoreChange, COUNT(a) as activityCount, MAX(a.createdAt) as lastActivityTime " +
           "FROM RankingUserActivity a " +
           "WHERE a.createdAt >= :startDate " +
           "GROUP BY a.userId " +
           "HAVING SUM(a.points) >= :threshold " +
           "ORDER BY totalScoreChange DESC")
    List<Object[]> findSuspiciousActivities(@Param("startDate") LocalDateTime startDate, @Param("threshold") int threshold);

    /**
     * 특정 기간 내 시간대별 활동 수 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 시간대별 활동 수 목록 [hour, count]
     */
    @Query("SELECT HOUR(a.createdAt) as hour, COUNT(a) as count " +
           "FROM RankingUserActivity a " +
           "WHERE a.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY HOUR(a.createdAt)")
    List<Object[]> countActivitiesByHourOfDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 