package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.ranking.RankingLeaderboardPort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingLeaderboard;
import com.j30n.stoblyx.domain.repository.RankingLeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 랭킹 리더보드 관련 아웃 어댑터 구현체
 */
@Component
public class RankingLeaderboardAdapter implements RankingLeaderboardPort {

    private static final String REDIS_LEADERBOARD_KEY = "realtime:leaderboard";

    private final RankingLeaderboardRepository rankingLeaderboardRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    public RankingLeaderboardAdapter(RankingLeaderboardRepository rankingLeaderboardRepository, 
                                    RedisTemplate<String, String> redisTemplate) {
        this.rankingLeaderboardRepository = rankingLeaderboardRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 랭킹 정보 저장
     *
     * @param leaderboard 랭킹 정보
     * @return 저장된 랭킹 정보
     */
    @Override
    public RankingLeaderboard saveLeaderboard(RankingLeaderboard leaderboard) {
        return rankingLeaderboardRepository.save(leaderboard);
    }

    /**
     * 랭킹 정보 목록 저장
     *
     * @param leaderboards 랭킹 정보 목록
     * @return 저장된 랭킹 정보 목록
     */
    @Override
    public List<RankingLeaderboard> saveAllLeaderboards(List<RankingLeaderboard> leaderboards) {
        return rankingLeaderboardRepository.saveAll(leaderboards);
    }

    /**
     * 리더보드 타입과 기간으로 랭킹 정보 조회
     *
     * @param leaderboardType 리더보드 타입 (DAILY, WEEKLY, MONTHLY)
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보 목록
     */
    @Override
    public List<RankingLeaderboard> findByLeaderboardTypeAndPeriod(String leaderboardType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingLeaderboardRepository.findByLeaderboardTypeAndPeriodStartDateAndPeriodEndDate(
            leaderboardType, startDate, endDate);
    }

    /**
     * 특정 기간의 리더보드에서 상위 N명의 랭킹 정보 조회
     *
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @param limit           조회할 사용자 수
     * @return 랭킹 정보 목록
     */
    @Override
    public List<RankingLeaderboard> findTopRankingsByPeriod(String leaderboardType, LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return rankingLeaderboardRepository.findTopRankingsByPeriod(
            leaderboardType, startDate, endDate, PageRequest.of(0, limit));
    }

    /**
     * 특정 사용자의 특정 기간 랭킹 정보 조회
     *
     * @param userId          사용자 ID
     * @param leaderboardType 리더보드 타입
     * @param startDate       시작 일시
     * @param endDate         종료 일시
     * @return 랭킹 정보
     */
    @Override
    public RankingLeaderboard findByUserIdAndLeaderboardTypeAndPeriod(Long userId, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingLeaderboardRepository.findByUserIdAndLeaderboardTypeAndPeriodStartDateAndPeriodEndDate(
            userId, leaderboardType, startDate, endDate);
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
    public List<RankingLeaderboard> findByRankTypeAndLeaderboardTypeAndPeriod(RankType rankType, String leaderboardType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingLeaderboardRepository.findByRankTypeAndLeaderboardTypeAndPeriodStartDateAndPeriodEndDate(
            rankType, leaderboardType, startDate, endDate);
    }

    /**
     * 특정 기간의 모든 리더보드 삭제
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     */
    @Override
    public void deleteByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        rankingLeaderboardRepository.deleteByPeriodStartDateAndPeriodEndDate(startDate, endDate);
    }

    /**
     * Redis에 실시간 랭킹 업데이트
     *
     * @param userId 사용자 ID
     * @param score  점수
     */
    @Override
    public void updateRedisRanking(Long userId, int score) {
        redisTemplate.opsForZSet().add(REDIS_LEADERBOARD_KEY, userId.toString(), score);
    }

    /**
     * Redis에서 상위 N명의 실시간 랭킹 조회
     *
     * @param limit 조회할 사용자 수
     * @return 사용자 ID와 점수 맵
     */
    @Override
    public Map<Long, Double> getTopRedisRankings(int limit) {
        Set<ZSetOperations.TypedTuple<String>> rankings = redisTemplate.opsForZSet()
            .reverseRangeWithScores(REDIS_LEADERBOARD_KEY, 0, limit - 1);

        if (rankings == null) {
            return new HashMap<>();
        }

        Map<Long, Double> result = new HashMap<>();

        for (ZSetOperations.TypedTuple<String> entry : rankings) {
            String userId = entry.getValue();
            Double score = entry.getScore();

            if (userId != null && score != null) {
                result.put(Long.parseLong(userId), score);
            }
        }

        return result;
    }

    /**
     * Redis에서 특정 사용자의 실시간 랭킹 조회
     *
     * @param userId 사용자 ID
     * @return 랭킹 정보 (순위, 점수)
     */
    @Override
    public Map<String, Object> getUserRedisRanking(Long userId) {
        Double score = redisTemplate.opsForZSet().score(REDIS_LEADERBOARD_KEY, userId.toString());
        Long rank = redisTemplate.opsForZSet().reverseRank(REDIS_LEADERBOARD_KEY, userId.toString());

        if (score == null || rank == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rank", rank.intValue() + 1); // Redis는 0부터 시작하므로 1을 더함
        result.put("score", score);

        return result;
    }
} 