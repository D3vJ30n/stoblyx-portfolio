package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingLeaderboard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 랭킹 리더보드 정보 레포지토리
 */
@Repository
public interface RankingLeaderboardRepository extends JpaRepository<RankingLeaderboard, Long> {

    /**
     * 리더보드 타입과 기간으로 랭킹 정보 조회
     * 
     * @param leaderboardType 리더보드 타입 (DAILY, WEEKLY, MONTHLY)
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> findByLeaderboardTypeAndPeriodStartDateAndPeriodEndDate(
            String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간의 리더보드에서 상위 N명의 랭킹 정보 조회
     * 
     * @param leaderboardType 리더보드 타입
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param pageable 페이징 정보
     * @return 랭킹 정보 목록
     */
    @Query("SELECT r FROM RankingLeaderboard r WHERE r.leaderboardType = :type " +
           "AND r.periodStartDate = :startDate AND r.periodEndDate = :endDate " +
           "ORDER BY r.score DESC")
    List<RankingLeaderboard> findTopRankingsByPeriod(
            @Param("type") String leaderboardType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * 특정 사용자의 특정 기간 랭킹 정보 조회
     * 
     * @param userId 사용자 ID
     * @param leaderboardType 리더보드 타입
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 랭킹 정보
     */
    RankingLeaderboard findByUserIdAndLeaderboardTypeAndPeriodStartDateAndPeriodEndDate(
            Long userId, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 랭크 타입의 사용자 랭킹 정보 조회
     * 
     * @param rankType 랭크 타입
     * @param leaderboardType 리더보드 타입
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> findByRankTypeAndLeaderboardTypeAndPeriodStartDateAndPeriodEndDate(
            RankType rankType, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간의 모든 리더보드 삭제
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     */
    void deleteByPeriodStartDateAndPeriodEndDate(LocalDateTime startDate, LocalDateTime endDate);
} 