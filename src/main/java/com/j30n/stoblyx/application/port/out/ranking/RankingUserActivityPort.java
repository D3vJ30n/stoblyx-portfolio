package com.j30n.stoblyx.application.port.out.ranking;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 활동 관련 포트 아웃 인터페이스
 */
public interface RankingUserActivityPort {

    /**
     * 사용자 활동 기록 저장
     *
     * @param activity 활동 기록
     * @return 저장된 활동 기록
     */
    RankingUserActivity saveActivity(RankingUserActivity activity);

    /**
     * 사용자 ID로 활동 기록 조회
     *
     * @param userId 사용자 ID
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> findByUserId(Long userId);

    /**
     * 사용자 ID와 활동 유형으로 활동 기록 조회
     *
     * @param userId       사용자 ID
     * @param activityType 활동 유형
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> findByUserIdAndActivityType(Long userId, ActivityType activityType);

    /**
     * 특정 기간 내 사용자 활동 기록 조회
     *
     * @param userId    사용자 ID
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 IP 주소에서 발생한 활동 기록 조회
     *
     * @param ipAddress IP 주소
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> findByIpAddressAndCreatedAtBetween(String ipAddress, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 IP 주소에서 발생한 특정 활동 유형의 기록 조회
     *
     * @param ipAddress    IP 주소
     * @param activityType 활동 유형
     * @param startDate    시작 일시
     * @param endDate      종료 일시
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> findByIpAddressAndActivityTypeAndCreatedAtBetween(String ipAddress, ActivityType activityType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 대상에 대한 활동 기록 조회
     *
     * @param targetId   대상 ID
     * @param targetType 대상 유형
     * @return 활동 기록 목록
     */
    List<RankingUserActivity> findByTargetIdAndTargetType(Long targetId, String targetType);
} 