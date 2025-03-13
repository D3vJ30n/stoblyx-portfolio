package com.j30n.stoblyx.application.port.in.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingActivityResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingScoreResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingStatisticsResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자의 랭킹 시스템 관리를 위한 유스케이스 인터페이스
 */
public interface AdminRankingUseCase {

    /**
     * 의심스러운 활동이 있는 사용자 목록 조회
     * 
     * @param threshold 점수 변화 임계값
     * @return 의심스러운 활동이 있는 사용자 목록
     */
    List<AdminRankingScoreResponse> findUsersWithSuspiciousActivity(int threshold);
    
    /**
     * 특정 기간 내 비정상적인 활동 패턴 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param activityThreshold 활동 횟수 임계값
     * @return 비정상적인 활동 패턴 목록
     */
    List<AdminRankingActivityResponse> findAbnormalActivityPatterns(LocalDateTime startDate, LocalDateTime endDate, int activityThreshold);
    
    /**
     * 특정 기간 내 활동 내역 조회
     * 
     * @param userId 사용자 ID (선택적)
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 내역 목록
     */
    List<AdminRankingActivityResponse> findActivitiesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 사용자 점수 수동 조정
     * 
     * @param userId 사용자 ID
     * @param scoreAdjustment 점수 조정값
     * @param reason 조정 사유
     * @return 조정된 사용자 점수 정보
     */
    AdminRankingScoreResponse adjustUserScore(Long userId, int scoreAdjustment, String reason);
    
    /**
     * 사용자 계정 정지 처리
     * 
     * @param userId 사용자 ID
     * @param reason 정지 사유
     * @return 정지된 사용자 점수 정보
     */
    AdminRankingScoreResponse suspendUserAccount(Long userId, String reason);
    
    /**
     * 사용자 계정 정지 해제
     * 
     * @param userId 사용자 ID
     * @return 정지 해제된 사용자 점수 정보
     */
    AdminRankingScoreResponse unsuspendUserAccount(Long userId);
    
    /**
     * 랭킹 시스템 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 랭킹 시스템 통계 정보
     */
    AdminRankingStatisticsResponse getRankingStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 랭킹 시스템 설정 업데이트
     * 
     * @param settingKey 설정 키
     * @param settingValue 설정 값
     * @return 업데이트 성공 여부
     */
    boolean updateRankingSystemSetting(String settingKey, String settingValue);
} 