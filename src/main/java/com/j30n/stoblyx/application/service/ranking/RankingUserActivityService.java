package com.j30n.stoblyx.application.service.ranking;

import com.j30n.stoblyx.application.port.in.ranking.RankingUserActivityUseCase;
import com.j30n.stoblyx.application.port.out.ranking.RankingUserActivityPort;
import com.j30n.stoblyx.application.port.out.ranking.RankingUserScorePort;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    @Autowired
    private RankingUserActivityPort rankingUserActivityPort;

    @Autowired
    private RankingUserScorePort rankingUserScorePort;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 사용자 활동 기록 생성
     * 활동 유형에 따라 점수를 계산하고, 사용자 점수를 업데이트
     *
     * @param userId       사용자 ID
     * @param targetId     대상 ID
     * @param targetType   대상 유형
     * @param activityType 활동 유형
     * @param ipAddress    IP 주소
     * @return 생성된 활동 기록
     */
    @Override
    @Transactional
    public RankingUserActivity createActivity(Long userId, Long targetId, String targetType, ActivityType activityType, String ipAddress) {
        // 활동 기록 생성
        RankingUserActivity activity = RankingUserActivity.builder()
            .userId(userId)
            .targetId(targetId)
            .targetType(targetType)
            .activityType(activityType)
            .scoreChange(activityType.getScoreWeight())
            .ipAddress(ipAddress)
            .build();

        // 활동 기록 저장
        RankingUserActivity savedActivity = rankingUserActivityPort.saveActivity(activity);

        // 사용자 점수 업데이트
        updateUserScore(userId, activityType.getScoreWeight());

        // 활동 이벤트 발행
        publishActivityEvent(savedActivity);

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
     * 특정 IP 주소에서 발생한 활동 기록 조회
     *
     * @param ipAddress IP 주소
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getActivitiesByIpAddress(String ipAddress, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityPort.findByIpAddressAndCreatedAtBetween(ipAddress, startDate, endDate);
    }

    /**
     * 특정 IP 주소에서 발생한 특정 활동 유형의 기록 조회
     *
     * @param ipAddress    IP 주소
     * @param activityType 활동 유형
     * @param startDate    시작 일시
     * @param endDate      종료 일시
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getActivitiesByIpAddressAndType(String ipAddress, ActivityType activityType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityPort.findByIpAddressAndActivityTypeAndCreatedAtBetween(ipAddress, activityType, startDate, endDate);
    }

    /**
     * 특정 대상에 대한 활동 기록 조회
     *
     * @param targetId   대상 ID
     * @param targetType 대상 유형
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> getActivitiesByTarget(Long targetId, String targetType) {
        return rankingUserActivityPort.findByTargetIdAndTargetType(targetId, targetType);
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

        RankingUserScore userScore;
        if (userScoreOpt.isPresent()) {
            userScore = userScoreOpt.get();

            // 이전 랭크 저장
            RankType previousRankType = userScore.getRankType();

            // 가중 이동 평균(EWMA) 알고리즘 적용
            userScore.updateScoreWithEWMA(scoreChange, 0.2);

            // 점수 업데이트 후 저장
            userScore = rankingUserScorePort.saveUserScore(userScore);

            // 랭크 변경 이벤트 발행
            if (previousRankType != userScore.getRankType()) {
                publishRankChangeEvent(userId, previousRankType, userScore.getRankType());
            }
        } else {
            // 사용자 점수 정보가 없는 경우 새로 생성
            userScore = RankingUserScore.builder()
                .userId(userId)
                .currentScore(1000 + scoreChange) // 초기 점수 + 활동 점수
                .previousScore(1000)
                .rankType(RankType.fromScore(1000 + scoreChange))
                .suspiciousActivity(false)
                .reportCount(0)
                .accountSuspended(false)
                .build();

            // 점수 정보 저장
            userScore = rankingUserScorePort.saveUserScore(userScore);

            // 신규 사용자 이벤트 발행
            publishNewUserEvent(userId, userScore.getRankType());
        }
    }

    /**
     * 활동 이벤트 발행
     *
     * @param activity 활동 기록
     */
    private void publishActivityEvent(RankingUserActivity activity) {
        // 활동 이벤트 발행 로직
        // eventPublisher.publishEvent(new ActivityEvent(activity));
    }

    /**
     * 랭크 변경 이벤트 발행
     *
     * @param userId           사용자 ID
     * @param previousRankType 이전 랭크 타입
     * @param newRankType      새 랭크 타입
     */
    private void publishRankChangeEvent(Long userId, RankType previousRankType, RankType newRankType) {
        // 랭크 변경 이벤트 발행 로직
        // eventPublisher.publishEvent(new RankChangeEvent(userId, previousRankType, newRankType));
    }

    /**
     * 신규 사용자 이벤트 발행
     *
     * @param userId   사용자 ID
     * @param rankType 랭크 타입
     */
    private void publishNewUserEvent(Long userId, RankType rankType) {
        // 신규 사용자 이벤트 발행 로직
        // eventPublisher.publishEvent(new NewUserEvent(userId, rankType));
    }
} 