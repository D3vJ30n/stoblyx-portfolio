package com.j30n.stoblyx.adapter.out.persistence.gamification;

import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import com.j30n.stoblyx.domain.repository.GamificationRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 어댑터 구현
 */
@Component
@RequiredArgsConstructor
public class GamificationRewardAdapter implements GamificationRewardPort {

    private final GamificationRewardRepository gamificationRewardRepository;

    /**
     * 보상 정보를 저장합니다.
     *
     * @param reward 저장할 보상 정보
     * @return 저장된 보상 정보
     */
    @Override
    public GamificationReward saveReward(GamificationReward reward) {
        return gamificationRewardRepository.save(reward);
    }

    /**
     * 여러 보상 정보를 저장합니다.
     *
     * @param rewards 저장할 보상 정보 목록
     * @return 저장된 보상 정보 목록
     */
    @Override
    public List<GamificationReward> saveAllRewards(List<GamificationReward> rewards) {
        return gamificationRewardRepository.saveAll(rewards);
    }

    /**
     * 사용자 ID로 보상 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 보상 목록
     */
    @Override
    public List<GamificationReward> findByUserId(Long userId) {
        return gamificationRewardRepository.findByUserId(userId);
    }

    /**
     * 사용자 ID와 보상 유형으로 보상 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param rewardType 보상 유형
     * @return 특정 유형의 사용자 보상 목록
     */
    @Override
    public List<GamificationReward> findByUserIdAndRewardType(Long userId, RewardType rewardType) {
        return gamificationRewardRepository.findByUserIdAndRewardType(userId, rewardType);
    }

    /**
     * ID로 보상 정보를 조회합니다.
     *
     * @param rewardId 보상 ID
     * @return 보상 정보
     * @throws IllegalArgumentException 해당 ID의 보상이 없을 경우
     */
    @Override
    public GamificationReward findById(Long rewardId) {
        return gamificationRewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 보상이 존재하지 않습니다."));
    }

    /**
     * 특정 기간 내에 생성된 보상을 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 내 생성된 보상 목록
     */
    @Override
    public List<GamificationReward> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return gamificationRewardRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * 만료된 보상 목록을 조회합니다.
     *
     * @param currentDate 현재 날짜시간
     * @return 만료된 보상 목록
     */
    @Override
    public List<GamificationReward> findExpiredRewards(LocalDateTime currentDate) {
        return gamificationRewardRepository.findExpiredRewards(currentDate);
    }

    /**
     * 특정 유형의 보상 정보를 조회합니다.
     *
     * @param rewardType 보상 유형
     * @return 해당 유형의 보상 목록
     */
    @Override
    public List<GamificationReward> findByRewardType(RewardType rewardType) {
        return gamificationRewardRepository.findByRewardType(rewardType);
    }

    /**
     * 수령하지 않은 보상 목록을 조회합니다.
     *
     * @return 수령하지 않은 보상 목록
     */
    @Override
    public List<GamificationReward> findByIsClaimedFalse() {
        return gamificationRewardRepository.findByIsClaimedFalse();
    }
    
    /**
     * 보상 ID로 존재 여부를 확인합니다.
     *
     * @param rewardId 보상 ID
     * @return 존재 여부
     */
    public boolean existsById(Long rewardId) {
        return gamificationRewardRepository.existsById(rewardId);
    }
    
    /**
     * 보상을 삭제합니다.
     *
     * @param rewardId 보상 ID
     */
    public void deleteReward(Long rewardId) {
        gamificationRewardRepository.deleteById(rewardId);
    }
} 