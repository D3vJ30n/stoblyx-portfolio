package com.j30n.stoblyx.adapter.out.persistence.gamification;

import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import com.j30n.stoblyx.domain.repository.GamificationRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 포트 구현체
 * 헥사고날 아키텍처의 어댑터 역할
 */
@Component
@RequiredArgsConstructor
public class GamificationRewardAdapter implements GamificationRewardPort {

    private final GamificationRewardRepository gamificationRewardRepository;

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
    public List<GamificationReward> findByUserIdAndRankType(Long userId, RankType rankType) {
        return gamificationRewardRepository.findByUserIdAndRankType(userId, rankType);
    }

    @Override
    public GamificationReward findById(Long rewardId) {
        return gamificationRewardRepository.findById(rewardId)
            .orElseThrow(() -> new IllegalArgumentException("보상 정보를 찾을 수 없습니다: " + rewardId));
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
    public List<GamificationReward> findByRankType(RankType rankType) {
        return gamificationRewardRepository.findByRankType(rankType);
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