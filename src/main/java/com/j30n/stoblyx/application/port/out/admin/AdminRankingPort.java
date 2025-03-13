package com.j30n.stoblyx.application.port.out.admin;

import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.model.RankingUserScore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 관리자의 랭킹 시스템 관리를 위한 포트 아웃 인터페이스
 */
public interface AdminRankingPort {

    /**
     * 의심스러운 활동이 있는 사용자 목록 조회
     * 
     * @param threshold 점수 변화 임계값
     * @return 의심스러운 활동이 있는 사용자 목록
     */
    List<RankingUserScore> findUsersWithSuspiciousActivity(int threshold);
    
    /**
     * 특정 기간 내 비정상적인 활동 패턴 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param activityThreshold 활동 횟수 임계값
     * @return 비정상적인 활동 패턴 목록
     */
    List<RankingUserActivity> findAbnormalActivityPatterns(LocalDateTime startDate, LocalDateTime endDate, int activityThreshold);
    
    /**
     * 특정 기간 내 활동 내역 조회
     * 
     * @param userId 사용자 ID (선택적)
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 내역 목록
     */
    List<RankingUserActivity> findActivitiesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 사용자 점수 수동 조정
     * 
     * @param userId 사용자 ID
     * @param scoreAdjustment 점수 조정값
     * @param reason 조정 사유
     * @return 조정된 사용자 점수 정보
     */
    RankingUserScore adjustUserScore(Long userId, int scoreAdjustment, String reason);
    
    /**
     * 사용자 계정 정지 처리
     * 
     * @param userId 사용자 ID
     * @param reason 정지 사유
     * @return 정지된 사용자 점수 정보
     */
    RankingUserScore suspendUserAccount(Long userId, String reason);
    
    /**
     * 사용자 계정 정지 해제
     * 
     * @param userId 사용자 ID
     * @return 정지 해제된 사용자 점수 정보
     */
    RankingUserScore unsuspendUserAccount(Long userId);
    
    /**
     * 랭크 타입별 사용자 분포 통계 조회
     * 
     * @return 랭크 타입별 사용자 수 맵
     */
    Map<RankType, Long> getRankDistributionStatistics();
    
    /**
     * 활동 유형별 발생 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 유형별 발생 횟수 맵
     */
    Map<ActivityType, Long> getActivityTypeStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 시간대별 활동 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 시간대별 활동 횟수 맵
     */
    Map<Integer, Long> getActivityByHourStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 랭킹 시스템 설정 업데이트
     * 
     * @param settingKey 설정 키
     * @param settingValue 설정 값
     * @return 업데이트 성공 여부
     */
    boolean updateRankingSystemSetting(String settingKey, String settingValue);
} 