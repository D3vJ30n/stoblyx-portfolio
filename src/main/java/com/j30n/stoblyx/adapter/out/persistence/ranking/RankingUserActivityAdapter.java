package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.ranking.RankingUserActivityPort;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.repository.RankingUserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 활동 관련 아웃 어댑터 구현체
 */
@Component
public class RankingUserActivityAdapter implements RankingUserActivityPort {

    @Autowired
    private RankingUserActivityRepository rankingUserActivityRepository;

    /**
     * 사용자 활동 기록 저장
     *
     * @param activity 활동 기록
     * @return 저장된 활동 기록
     */
    @Override
    public RankingUserActivity saveActivity(RankingUserActivity activity) {
        return rankingUserActivityRepository.save(activity);
    }

    /**
     * 사용자 ID로 활동 기록 조회
     *
     * @param userId 사용자 ID
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> findByUserId(Long userId) {
        return rankingUserActivityRepository.findByUserId(userId);
    }

    /**
     * 사용자 ID와 활동 유형으로 활동 기록 조회
     *
     * @param userId       사용자 ID
     * @param activityType 활동 유형
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> findByUserIdAndActivityType(Long userId, ActivityType activityType) {
        return rankingUserActivityRepository.findByUserIdAndActivityType(userId, activityType);
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
    public List<RankingUserActivity> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
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
    public List<RankingUserActivity> findByIpAddressAndCreatedAtBetween(String ipAddress, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityRepository.findByIpAddressAndCreatedAtBetween(ipAddress, startDate, endDate);
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
    public List<RankingUserActivity> findByIpAddressAndActivityTypeAndCreatedAtBetween(String ipAddress, ActivityType activityType, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityRepository.findByIpAddressAndActivityTypeAndCreatedAtBetween(ipAddress, activityType, startDate, endDate);
    }

    /**
     * 특정 대상에 대한 활동 기록 조회
     *
     * @param targetId   대상 ID
     * @param targetType 대상 유형
     * @return 활동 기록 목록
     */
    @Override
    public List<RankingUserActivity> findByTargetIdAndTargetType(Long targetId, String targetType) {
        return rankingUserActivityRepository.findByTargetIdAndTargetType(targetId, targetType);
    }
} 