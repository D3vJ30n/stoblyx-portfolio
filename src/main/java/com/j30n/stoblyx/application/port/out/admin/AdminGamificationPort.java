package com.j30n.stoblyx.application.port.out.admin;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 관리자의 게이미피케이션 시스템 관리를 위한 포트 아웃 인터페이스
 */
public interface AdminGamificationPort {

    /**
     * 특정 사용자의 보상 내역 조회
     * 
     * @param userId 사용자 ID
     * @return 보상 내역 목록
     */
    List<GamificationReward> findRewardsByUserId(Long userId);
    
    /**
     * 특정 보상 유형의 보상 내역 조회
     * 
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    List<GamificationReward> findRewardsByType(RewardType rewardType);
    
    /**
     * 특정 랭크 타입의 보상 내역 조회
     * 
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    List<GamificationReward> findRewardsByRankType(RankType rankType);
    
    /**
     * 특정 기간 내 생성된 보상 내역 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 보상 내역 목록
     */
    List<GamificationReward> findRewardsByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 만료된 보상 내역 조회
     * 
     * @param currentDate 현재 일시
     * @return 보상 내역 목록
     */
    List<GamificationReward> findExpiredRewards(LocalDateTime currentDate);
    
    /**
     * 아직 지급되지 않은 보상 내역 조회
     * 
     * @return 보상 내역 목록
     */
    List<GamificationReward> findUnclaimedRewards();
    
    /**
     * 수동으로 보상 생성
     * 
     * @param userId 사용자 ID
     * @param rewardType 보상 유형
     * @param rewardAmount 보상 양
     * @param description 보상 설명
     * @param expiryDate 만료 일시
     * @return 생성된 보상 내역
     */
    GamificationReward createReward(Long userId, RewardType rewardType, Integer rewardAmount, String description, LocalDateTime expiryDate);
    
    /**
     * 보상 내역 수정
     * 
     * @param rewardId 보상 ID
     * @param rewardAmount 보상 양
     * @param description 보상 설명
     * @param expiryDate 만료 일시
     * @return 수정된 보상 내역
     */
    GamificationReward updateReward(Long rewardId, Integer rewardAmount, String description, LocalDateTime expiryDate);
    
    /**
     * 보상 내역 삭제
     * 
     * @param rewardId 보상 ID
     * @return 삭제 성공 여부
     */
    boolean deleteReward(Long rewardId);
    
    /**
     * 보상 지급 처리
     * 
     * @param rewardId 보상 ID
     * @return 지급 처리된 보상 내역
     */
    GamificationReward claimReward(Long rewardId);
    
    /**
     * 보상 유형별 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 보상 유형별 지급 횟수 맵
     */
    Map<RewardType, Long> getRewardTypeStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 랭크 타입별 보상 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 랭크 타입별 보상 지급 횟수 맵
     */
    Map<RankType, Long> getRankTypeRewardStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 게이미피케이션 시스템 설정 업데이트
     * 
     * @param settingKey 설정 키
     * @param settingValue 설정 값
     * @return 업데이트 성공 여부
     */
    boolean updateGamificationSystemSetting(String settingKey, String settingValue);
} 