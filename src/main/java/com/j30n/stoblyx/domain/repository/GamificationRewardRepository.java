package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 정보 레포지토리
 */
@Repository
public interface GamificationRewardRepository extends JpaRepository<GamificationReward, Long> {

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
     * @param userId 사용자 ID
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByUserIdAndRewardType(Long userId, RewardType rewardType);

    /**
     * 사용자 ID와 랭크 타입으로 보상 내역 조회
     * 
     * @param userId 사용자 ID
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    List<GamificationReward> findByUserIdAndRankType(Long userId, RankType rankType);

    /**
     * 특정 기간 내에 생성된 보상 내역 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 보상 내역 목록
     */
    @Query("SELECT g FROM GamificationReward g WHERE g.createdAt BETWEEN :startDate AND :endDate")
    List<GamificationReward> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 만료된 보상 내역 조회
     * 
     * @param currentDate 현재 일시
     * @return 보상 내역 목록
     */
    @Query("SELECT g FROM GamificationReward g WHERE g.expiryDate < :currentDate AND g.isClaimed = false")
    List<GamificationReward> findExpiredRewards(@Param("currentDate") LocalDateTime currentDate);

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