package com.j30n.stoblyx.application.service.ranking;

import com.j30n.stoblyx.application.port.in.ranking.RankingUserScoreUseCase;
import com.j30n.stoblyx.application.port.out.ranking.RankingUserScorePort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 점수 관련 서비스 구현체
 */
@Service
public class RankingUserScoreService implements RankingUserScoreUseCase {

    private final RankingUserScorePort rankingUserScorePort;

    public RankingUserScoreService(RankingUserScorePort rankingUserScorePort) {
        this.rankingUserScorePort = rankingUserScorePort;
    }

    /**
     * 사용자 점수 정보 조회
     *
     * @param userId 사용자 ID
     * @return 점수 정보
     */
    @Override
    public RankingUserScore getUserScore(Long userId) {
        return rankingUserScorePort.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("User score not found for userId: " + userId));
    }

    /**
     * 사용자 점수 정보 생성 또는 업데이트
     * 가중 이동 평균(EWMA) 알고리즘을 적용하여 점수 계산
     *
     * @param userId        사용자 ID
     * @param activityScore 활동 점수
     * @return 업데이트된 점수 정보
     */
    @Override
    @Transactional
    public RankingUserScore updateUserScore(Long userId, int activityScore) {
        Optional<RankingUserScore> userScoreOpt = rankingUserScorePort.findByUserId(userId);

        RankingUserScore userScore;
        if (userScoreOpt.isPresent()) {
            userScore = userScoreOpt.get();

            // 가중 이동 평균(EWMA) 알고리즘 적용
            userScore.updateScoreWithEWMA(activityScore, 0.2);

            // 점수 업데이트 후 저장
            userScore = rankingUserScorePort.saveUserScore(userScore);

            // 급격한 점수 상승 감지
            detectSuspiciousScoreIncrease(userScore);
        } else {
            // 사용자 점수 정보가 없는 경우 새로 생성
            userScore = RankingUserScore.builder()
                .userId(userId)
                .currentScore(1000 + activityScore) // 초기 점수 + 활동 점수
                .previousScore(1000)
                .rankType(RankType.fromScore(1000 + activityScore))
                .suspiciousActivity(false)
                .reportCount(0)
                .accountSuspended(false)
                .build();

            // 점수 정보 저장
            userScore = rankingUserScorePort.saveUserScore(userScore);
        }

        return userScore;
    }

    /**
     * 사용자 신고 처리
     * 신고 횟수를 증가시키고, 임계값을 초과하면 계정을 정지
     *
     * @param userId              사용자 ID
     * @param suspensionThreshold 계정 정지 임계값
     * @return 업데이트된 점수 정보
     */
    @Override
    @Transactional
    public RankingUserScore reportUser(Long userId, int suspensionThreshold) {
        RankingUserScore userScore = rankingUserScorePort.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("User score not found for userId: " + userId));

        // 신고 횟수 증가 및 계정 정지 여부 확인
        userScore.incrementReportCount(suspensionThreshold);

        // 점수 정보 저장
        userScore = rankingUserScorePort.saveUserScore(userScore);

        return userScore;
    }

    /**
     * 비활동 사용자 점수 감소 처리
     * 특정 기간 동안 활동이 없는 사용자의 점수를 감소
     *
     * @param inactivityPeriod 비활동 기간 (일)
     * @param decayFactor      감소 계수
     * @return 업데이트된 점수 정보 목록
     */
    @Override
    @Transactional
    public List<RankingUserScore> decayInactiveUserScores(int inactivityPeriod, double decayFactor) {
        // 특정 기간 동안 활동이 없는 사용자 조회
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(inactivityPeriod);
        List<RankingUserScore> inactiveUsers = rankingUserScorePort.findInactiveUsersSince(cutoffDate);

        // 각 사용자의 점수 감소
        for (RankingUserScore userScore : inactiveUsers) {
            // 점수 감소 적용
            userScore.decayScoreForInactivity(decayFactor);
        }

        // 점수 정보 저장
        return rankingUserScorePort.saveAllUserScores(inactiveUsers);
    }

    /**
     * 특정 랭크 타입의 사용자 목록 조회
     *
     * @param rankType 랭크 타입
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> getUsersByRankType(RankType rankType) {
        return rankingUserScorePort.findByRankType(rankType);
    }

    /**
     * 상위 N명의 사용자 점수 정보 조회
     *
     * @param limit 조회할 사용자 수
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> getTopUsers(int limit) {
        return rankingUserScorePort.findTopUsersByScore(limit);
    }

    /**
     * 급격한 점수 상승이 있는 사용자 목록 조회
     *
     * @param threshold 점수 상승 임계값
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> getUsersWithSuspiciousScoreIncrease(int threshold) {
        return rankingUserScorePort.findUsersWithSuspiciousScoreIncrease(threshold);
    }

    /**
     * 계정이 정지된 사용자 목록 조회
     *
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> getSuspendedUsers() {
        return rankingUserScorePort.findByAccountSuspendedTrue();
    }

    /**
     * 급격한 점수 상승 감지
     *
     * @param userScore 사용자 점수 정보
     */
    private void detectSuspiciousScoreIncrease(RankingUserScore userScore) {
        // 이전 점수와 현재 점수의 차이 계산
        int scoreDifference = userScore.getCurrentScore() - userScore.getPreviousScore();

        // 임계값 설정 (예: 100점)
        int threshold = 100;

        // 점수 차이가 임계값을 초과하면 의심스러운 활동으로 표시
        if (scoreDifference > threshold) {
            userScore.setSuspiciousActivity(true);
        }
    }
} 