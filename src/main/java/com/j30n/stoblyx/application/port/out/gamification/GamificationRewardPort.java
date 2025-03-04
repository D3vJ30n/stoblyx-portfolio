package com.j30n.stoblyx.application.port.out.gamification;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 관련 포트 아웃 인터페이스
 */
public interface GamificationRewardPort {

    /**
     * 보상 정보 저장
     *
     * @param reward 보상 정보
     * @return 저장된 보상 정보
     */
    GamificationReward saveReward(GamificationReward reward);

    /**
     * 보상 정보 목록 저장
     *
     * @param rewards 보상 정보 목록
     * @return 저장된 보상 정보 목록
     */
    List<GamificationReward> saveAllRewards(List<GamificationReward> rewards);

    /**
     * 사용자 ID로 보상 내역 조회
     *
     * @param userId 사용자 ID
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByUserId(Long userId);

    /**
     * 사용자 ID와 보상 유형으로 보상 내역 조회
     *
     * @param userId     사용자 ID
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByUserIdAndRewardType(Long userId, RewardType rewardType);

    /**
     * 사용자 ID와 랭크 타입으로 보상 내역 조회
     *
     * @param userId   사용자 ID
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByUserIdAndRankType(Long userId, RankType rankType);

    /**
     * 보상 ID로 보상 정보 조회
     *
     * @param rewardId 보상 ID
     * @return 보상 정보
     */
    GamificationReward findById(Long rewardId);

    /**
     * 특정 기간 내에 생성된 보상 내역 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 만료된 보상 내역 조회
     *
     * @param currentDate 현재 일시
     * @return 보상 내역 목록
     */
    List<GamificationReward> findExpiredRewards(LocalDateTime currentDate);

    /**
     * 특정 랭크 타입의 보상 내역 조회
     *
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByRankType(RankType rankType);

    /**
     * 특정 보상 유형의 보상 내역 조회
     *
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByRewardType(RewardType rewardType);

    /**
     * 아직 지급되지 않은 보상 내역 조회
     *
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByIsClaimedFalse();
} 