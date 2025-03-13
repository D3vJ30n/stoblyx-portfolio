package com.j30n.stoblyx.application.port.in.gamification;

import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 관련 유스케이스 인터페이스
 */
public interface GamificationRewardUseCase {

    /**
     * 사용자 보상 생성
     *
     * @param userId            사용자 ID
     * @param rewardType        보상 유형
     * @param rewardAmount      보상 금액
     * @param rewardDescription 보상 설명
     * @param expiryDate        만료 일시
     * @return 생성된 보상 정보
     */
    GamificationReward createReward(Long userId, RewardType rewardType, Integer rewardAmount, String rewardDescription, LocalDateTime expiryDate);

    /**
     * 사용자 보상 내역 조회
     *
     * @param userId 사용자 ID
     * @return 보상 내역 목록
     */
    List<GamificationReward> getUserRewards(Long userId);

    /**
     * 사용자 보상 유형별 내역 조회
     *
     * @param userId     사용자 ID
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    List<GamificationReward> getUserRewardsByType(Long userId, RewardType rewardType);

    /**
     * 보상 지급 처리
     *
     * @param rewardId 보상 ID
     * @return 업데이트된 보상 정보
     */
    GamificationReward claimReward(Long rewardId);

    /**
     * 만료된 보상 내역 조회
     *
     * @return 보상 내역 목록
     */
    List<GamificationReward> getExpiredRewards();

    /**
     * 특정 보상 유형의 보상 내역 조회
     *
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    List<GamificationReward> getRewardsByType(RewardType rewardType);

    /**
     * 아직 지급되지 않은 보상 내역 조회
     *
     * @return 보상 내역 목록
     */
    List<GamificationReward> getUnclaimedRewards();
} 