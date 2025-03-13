package com.j30n.stoblyx.application.service.ranking;

import com.j30n.stoblyx.application.port.in.ranking.RankingUserActivityUseCase;
import com.j30n.stoblyx.application.port.out.ranking.RankingUserActivityPort;
import com.j30n.stoblyx.application.port.out.ranking.RankingUserScorePort;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 활동 관련 서비스 구현체
 */
@Service
public class RankingUserActivityService implements RankingUserActivityUseCase {

    private final RankingUserActivityPort rankingUserActivityPort;
    private final RankingUserScorePort rankingUserScorePort;
    
    /**
     * 생성자 주입
     * 
     * @param rankingUserActivityPort 사용자 활동 포트
     * @param rankingUserScorePort 사용자 점수 포트
     */
    public RankingUserActivityService(RankingUserActivityPort rankingUserActivityPort,
                                     RankingUserScorePort rankingUserScorePort) {
        this.rankingUserActivityPort = rankingUserActivityPort;
        this.rankingUserScorePort = rankingUserScorePort;
    }

    /**
     * 사용자 활동 기록 생성
     * 활동 유형에 따라 점수를 계산하고, 사용자 점수를 업데이트
     *
     * @param userId       사용자 ID
     * @param referenceId     참조 ID
     * @param referenceType   참조 유형
     * @param activityType 활동 유형
     * @return 생성된 활동 기록
     */
    @Override
    @Transactional
    public RankingUserActivity createActivity(Long userId, Long referenceId, String referenceType, ActivityType activityType) {
        // 활동 기록 생성
        RankingUserActivity activity = RankingUserActivity.builder()
            .userId(userId)
            .referenceId(referenceId)
            .referenceType(referenceType)
            .activityType(activityType)
            .points(activityType.getScoreWeight())
            .build();

        // 활동 기록 저장
        RankingUserActivity savedActivity = rankingUserActivityPort.saveActivity(activity);

        // 사용자 점수 업데이트
        updateUserScore(userId, activityType.getScoreWeight());

        return savedActivity;
    }

    /**
     * 사용자 활동 기록 조회
     *
     * @param userId 사용자 ID
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getUserActivities(Long userId) {
        return rankingUserActivityPort.findByUserId(userId);
    }

    /**
     * 사용자 활동 유형별 기록 조회
     *
     * @param userId       사용자 ID
     * @param activityType 활동 유형
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getUserActivitiesByType(Long userId, ActivityType activityType) {
        return rankingUserActivityPort.findByUserIdAndActivityType(userId, activityType);
    }

    /**
     * 특정 기간 내 사용자 활동 기록 조회
     *
     * @param userId    사용자 ID
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getUserActivitiesByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityPort.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    /**
     * 특정 참조 ID와 참조 유형에 대한 활동 기록 조회
     *
     * @param referenceId   참조 ID
     * @param referenceType 참조 유형
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getActivitiesByReference(Long referenceId, String referenceType) {
        return rankingUserActivityPort.findByReferenceIdAndReferenceType(referenceId, referenceType);
    }

    /**
     * 사용자 점수 업데이트
     * 가중 이동 평균(EWMA) 알고리즘을 적용하여 점수 계산
     *
     * @param userId      사용자 ID
     * @param scoreChange 점수 변화량
     */
    private void updateUserScore(Long userId, int scoreChange) {
        // 사용자 점수 정보 조회
        Optional<RankingUserScore> userScoreOpt = rankingUserScorePort.findByUserId(userId);

        if (userScoreOpt.isPresent()) {
            RankingUserScore userScore = userScoreOpt.get();

            // 가중 이동 평균(EWMA) 알고리즘 적용
            userScore.updateScoreWithEWMA(scoreChange, 0.2);

            // 점수 업데이트 후 저장
            rankingUserScorePort.saveUserScore(userScore);
        } else {
            // 사용자 점수 정보가 없는 경우 새로 생성
            RankingUserScore newUserScore = RankingUserScore.builder()
                .userId(userId)
                .currentScore(1000 + scoreChange) // 초기 점수 + 활동 점수
                .previousScore(1000)
                .rankType(RankType.fromScore(1000 + scoreChange))
                .suspiciousActivity(false)
                .reportCount(0)
                .accountSuspended(false)
                .build();

            // 점수 정보 저장
            rankingUserScorePort.saveUserScore(newUserScore);
        }
    }
} 