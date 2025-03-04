package com.j30n.stoblyx.application.port.out.ranking;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingLeaderboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 랭킹 리더보드 관련 포트 아웃 인터페이스
 */
public interface RankingLeaderboardPort {

    /**
     * 랭킹 정보 저장
     *
     * @param leaderboard 랭킹 정보
     * @return 저장된 랭킹 정보
     */
    RankingLeaderboard saveLeaderboard(RankingLeaderboard leaderboard);

    /**
     * 랭킹 정보 목록 저장
     *
     * @param leaderboards 랭킹 정보 목록
     * @return 저장된 랭킹 정보 목록
     */
    List<RankingLeaderboard> saveAllLeaderboards(List<RankingLeaderboard> leaderboards);

    /**
     * 리더보드 타입과 기간으로 랭킹 정보 조회
     *
     * @param leaderboardType 리더보드 타입 (DAILY, WEEKLY, MONTHLY)
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> findByLeaderboardTypeAndPeriod(String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간의 리더보드에서 상위 N명의 랭킹 정보 조회
     *
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @param limit           조회할 사용자 수
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> findTopRankingsByPeriod(String leaderboardType, LocalDateTime startDate, LocalDateTime endDate, int limit);

    /**
     * 특정 사용자의 특정 기간 랭킹 정보 조회
     *
     * @param userId          사용자 ID
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보
     */
    RankingLeaderboard findByUserIdAndLeaderboardTypeAndPeriod(Long userId, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 랭크 타입의 사용자 랭킹 정보 조회
     *
     * @param rankType        랭크 타입
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보 목록
     */
    List<RankingLeaderboard> findByRankTypeAndLeaderboardTypeAndPeriod(RankType rankType, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간의 모든 리더보드 삭제
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     */
    void deleteByPeriod(LocalDateTime startDate, LocalDateTime endDate);

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