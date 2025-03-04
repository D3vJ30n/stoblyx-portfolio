package com.j30n.stoblyx.application.service.ranking;

import com.j30n.stoblyx.application.port.in.ranking.RankingLeaderboardUseCase;
import com.j30n.stoblyx.application.port.out.ranking.RankingLeaderboardPort;
import com.j30n.stoblyx.application.port.out.ranking.RankingUserScorePort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingLeaderboard;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 랭킹 리더보드 관련 서비스 구현체
 */
@Service
public class RankingLeaderboardService implements RankingLeaderboardUseCase {

    @Autowired
    private RankingLeaderboardPort rankingLeaderboardPort;

    @Autowired
    private RankingUserScorePort rankingUserScorePort;

    /**
     * 일일 랭킹 생성
     *
     * @param date 날짜
     * @return 생성된 랭킹 정보 목록
     */
    @Override
    @Transactional
    public List<RankingLeaderboard> createDailyLeaderboard(LocalDateTime date) {
        // 해당 날짜의 시작과 끝 시간 설정
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(LocalTime.MAX);

        // 기존 리더보드 삭제
        rankingLeaderboardPort.deleteByPeriod(startOfDay, endOfDay);

        // 상위 100명의 사용자 점수 정보 조회
        List<RankingUserScore> topUsers = rankingUserScorePort.findTopUsersByScore(100);

        // 리더보드 생성
        List<RankingLeaderboard> leaderboards = createLeaderboardEntries(topUsers, "DAILY", startOfDay, endOfDay);

        // 리더보드 저장
        return rankingLeaderboardPort.saveAllLeaderboards(leaderboards);
    }

    /**
     * 주간 랭킹 생성
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 생성된 랭킹 정보 목록
     */
    @Override
    @Transactional
    public List<RankingLeaderboard> createWeeklyLeaderboard(LocalDateTime startDate, LocalDateTime endDate) {
        // 기존 리더보드 삭제
        rankingLeaderboardPort.deleteByPeriod(startDate, endDate);

        // 상위 100명의 사용자 점수 정보 조회
        List<RankingUserScore> topUsers = rankingUserScorePort.findTopUsersByScore(100);

        // 리더보드 생성
        List<RankingLeaderboard> leaderboards = createLeaderboardEntries(topUsers, "WEEKLY", startDate, endDate);

        // 리더보드 저장
        return rankingLeaderboardPort.saveAllLeaderboards(leaderboards);
    }

    /**
     * 월간 랭킹 생성
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 생성된 랭킹 정보 목록
     */
    @Override
    @Transactional
    public List<RankingLeaderboard> createMonthlyLeaderboard(LocalDateTime startDate, LocalDateTime endDate) {
        // 기존 리더보드 삭제
        rankingLeaderboardPort.deleteByPeriod(startDate, endDate);

        // 상위 100명의 사용자 점수 정보 조회
        List<RankingUserScore> topUsers = rankingUserScorePort.findTopUsersByScore(100);

        // 리더보드 생성
        List<RankingLeaderboard> leaderboards = createLeaderboardEntries(topUsers, "MONTHLY", startDate, endDate);

        // 리더보드 저장
        return rankingLeaderboardPort.saveAllLeaderboards(leaderboards);
    }

    /**
     * 특정 기간의 상위 N명 랭킹 조회
     *
     * @param leaderboardType 리더보드 타입 (DAILY, WEEKLY, MONTHLY)
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @param limit           조회할 사용자 수
     * @return 랭킹 정보 목록
     */
    @Override
    public List<RankingLeaderboard> getTopRankings(String leaderboardType, LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return rankingLeaderboardPort.findTopRankingsByPeriod(leaderboardType, startDate, endDate, limit);
    }

    /**
     * 특정 사용자의 랭킹 정보 조회
     *
     * @param userId          사용자 ID
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보
     */
    @Override
    public RankingLeaderboard getUserRanking(Long userId, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingLeaderboardPort.findByUserIdAndLeaderboardTypeAndPeriod(userId, leaderboardType, startDate, endDate);
    }

    /**
     * 특정 랭크 타입의 사용자 랭킹 정보 조회
     *
     * @param rankType        랭크 타입
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보 목록
     */
    @Override
    public List<RankingLeaderboard> getRankingsByRankType(RankType rankType, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingLeaderboardPort.findByRankTypeAndLeaderboardTypeAndPeriod(rankType, leaderboardType, startDate, endDate);
    }

    /**
     * Redis에 실시간 랭킹 업데이트
     *
     * @param userId 사용자 ID
     * @param score  점수
     */
    @Override
    public void updateRedisRanking(Long userId, int score) {
        rankingLeaderboardPort.updateRedisRanking(userId, score);
    }

    /**
     * Redis에서 상위 N명의 실시간 랭킹 조회
     *
     * @param limit 조회할 사용자 수
     * @return 사용자 ID와 점수 맵
     */
    @Override
    public Map<Long, Double> getTopRedisRankings(int limit) {
        return rankingLeaderboardPort.getTopRedisRankings(limit);
    }

    /**
     * Redis에서 특정 사용자의 실시간 랭킹 조회
     *
     * @param userId 사용자 ID
     * @return 랭킹 정보 (순위, 점수)
     */
    @Override
    public Map<String, Object> getUserRedisRanking(Long userId) {
        return rankingLeaderboardPort.getUserRedisRanking(userId);
    }

    /**
     * 리더보드 항목 생성
     *
     * @param userScores      사용자 점수 정보 목록
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 리더보드 항목 목록
     */
    private List<RankingLeaderboard> createLeaderboardEntries(List<RankingUserScore> userScores, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate) {
        List<RankingLeaderboard> leaderboards = new ArrayList<>();

        for (int i = 0; i < userScores.size(); i++) {
            RankingUserScore userScore = userScores.get(i);

            // 리더보드 항목 생성
            RankingLeaderboard leaderboard = RankingLeaderboard.builder()
                .userId(userScore.getUserId())
                .username("User_" + userScore.getUserId()) // 실제 사용자 이름으로 대체 필요
                .score(userScore.getCurrentScore())
                .rankType(userScore.getRankType())
                .leaderboardType(leaderboardType)
                .rankPosition(i + 1) // 1부터 시작하는 순위
                .periodStartDate(startDate)
                .periodEndDate(endDate)
                .build();

            leaderboards.add(leaderboard);
        }

        return leaderboards;
    }

    /**
     * 현재 주의 시작일과 종료일 계산
     *
     * @return 시작일과 종료일 배열
     */
    public LocalDateTime[] getCurrentWeekRange() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        return new LocalDateTime[]{
            startOfWeek.atStartOfDay(),
            endOfWeek.atTime(LocalTime.MAX)
        };
    }

    /**
     * 현재 월의 시작일과 종료일 계산
     *
     * @return 시작일과 종료일 배열
     */
    public LocalDateTime[] getCurrentMonthRange() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        return new LocalDateTime[]{
            startOfMonth.atStartOfDay(),
            endOfMonth.atTime(LocalTime.MAX)
        };
    }
} 