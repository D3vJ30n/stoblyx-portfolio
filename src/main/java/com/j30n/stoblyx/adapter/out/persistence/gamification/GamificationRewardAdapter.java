package com.j30n.stoblyx.adapter.out.persistence.gamification;

import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import com.j30n.stoblyx.domain.repository.GamificationRewardRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 어댑터 구현
 */
@Component
public class GamificationRewardAdapter implements GamificationRewardPort {

    private GamificationRewardRepository gamificationRewardRepository;

    @Override
    public GamificationReward saveReward(GamificationReward reward) {
        return gamificationRewardRepository.save(reward);
    }

    @Override
    public List<GamificationReward> saveAllRewards(List<GamificationReward> rewards) {
        return gamificationRewardRepository.saveAll(rewards);
    }

    @Override
    public List<GamificationReward> findByUserId(Long userId) {
        return gamificationRewardRepository.findByUserId(userId);
    }

    @Override
    public List<GamificationReward> findByUserIdAndRewardType(Long userId, RewardType rewardType) {
        return gamificationRewardRepository.findByUserIdAndRewardType(userId, rewardType);
    }

    @Override
    public GamificationReward findById(Long rewardId) {
        return gamificationRewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 보상이 존재하지 않습니다."));
    }

    @Override
    public List<GamificationReward> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return gamificationRewardRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<GamificationReward> findExpiredRewards(LocalDateTime currentDate) {
        return gamificationRewardRepository.findExpiredRewards(currentDate);
    }

    @Override
    public List<GamificationReward> findByRewardType(RewardType rewardType) {
        return gamificationRewardRepository.findByRewardType(rewardType);
    }

    @Override
    public List<GamificationReward> findByIsClaimedFalse() {
        return gamificationRewardRepository.findByIsClaimedFalse();
    }
} 