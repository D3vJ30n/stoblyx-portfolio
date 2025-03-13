package com.j30n.stoblyx.application.port.in.ranking;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 활동 관련 유스케이스 인터페이스
 */
public interface RankingUserActivityUseCase {

    /**
     * 사용자 활동 기록 생성
     *
     * @param userId       사용자 ID
     * @param referenceId     참조 ID
     * @param referenceType   참조 유형
     * @param activityType 활동 유형
     * @return 생성된 활동 기록
     */
    RankingUserActivity createActivity(Long userId, Long referenceId, String referenceType, ActivityType activityType);

    /**
     * 사용자 활동 기록 조회
     *
     * @param userId 사용자 ID
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> getUserActivities(Long userId);

    /**
     * 사용자 활동 유형별 기록 조회
     *
     * @param userId       사용자 ID
     * @param activityType 활동 유형
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> getUserActivitiesByType(Long userId, ActivityType activityType);

    /**
     * 특정 기간 내 사용자 활동 기록 조회
     *
     * @param userId    사용자 ID
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> getUserActivitiesByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 참조 ID와 참조 유형에 대한 활동 기록 조회
     *
     * @param referenceId   참조 ID
     * @param referenceType 참조 유형
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> getActivitiesByReference(Long referenceId, String referenceType);
} 