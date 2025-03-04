package com.j30n.stoblyx.application.port.in.ranking;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingLeaderboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 랭킹 리더보드 관련 유스케이스 인터페이스
 */
public interface RankingLeaderboardUseCase {

    /**
     * 일일 랭킹 생성
     *
     * @param date 날짜
     * @return 생성된 랭킹 정보 목록
     */
    List<RankingLeaderboard> createDailyLeaderboard(LocalDateTime date);

    /**
     * 주간 랭킹 생성
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 생성된 랭킹 정보 목록
     */
    List<RankingLeaderboard> createWeeklyLeaderboard(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 월간 랭킹 생성
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 생성된 랭킹 정보 목록
     */
    List<RankingLeaderboard> createMonthlyLeaderboard(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간의 상위 N명 랭킹 조회
     *
     * @param leaderboardType 리더보드 타입 (DAILY, WEEKLY, MONTHLY)
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @param limit           조회할 사용자 수
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> getTopRankings(String leaderboardType, LocalDateTime startDate, LocalDateTime endDate, int limit);

    /**
     * 특정 사용자의 랭킹 정보 조회
     *
     * @param userId          사용자 ID
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보
     */
    RankingLeaderboard getUserRanking(Long userId, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 랭크 타입의 사용자 랭킹 정보 조회
     *
     * @param rankType        랭크 타입
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> getRankingsByRankType(RankType rankType, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Redis에 실시간 랭킹 업데이트
     *
     * @param userId 사용자 ID
     * @param score  점수
     */
    void updateRedisRanking(Long userId, int score);

    /**
     * Redis에서 상위 N명의 실시간 랭킹 조회
     *
     * @param limit 조회할 사용자 수
     * @return 사용자 ID와 점수 맵
     */
    Map<Long, Double> getTopRedisRankings(int limit);

    /**
     * Redis에서 특정 사용자의 실시간 랭킹 조회
     *
     * @param userId 사용자 ID
     * @return 랭킹 정보 (순위, 점수)
     */
    Map<String, Object> getUserRedisRanking(Long userId);
} 