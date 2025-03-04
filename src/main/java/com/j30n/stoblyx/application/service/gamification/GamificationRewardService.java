package com.j30n.stoblyx.application.service.gamification;

import com.j30n.stoblyx.application.port.in.gamification.GamificationRewardUseCase;
import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게이미피케이션 보상 관련 서비스 구현체
 */
@Service
public class GamificationRewardService implements GamificationRewardUseCase {

    @Autowired
    private GamificationRewardPort gamificationRewardPort;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 사용자 보상 생성
     *
     * @param userId            사용자 ID
     * @param rankType          랭크 타입
     * @param rewardType        보상 유형
     * @param rewardAmount      보상 금액
     * @param rewardDescription 보상 설명
     * @param expiryDate        만료 일시
     * @return 생성된 보상 정보
     */
    @Override
    @Transactional
    public GamificationReward createReward(Long userId, RankType rankType, RewardType rewardType, Integer rewardAmount, String rewardDescription, LocalDateTime expiryDate) {
        // 보상 정보 생성
        GamificationReward reward = GamificationReward.builder()
            .userId(userId)
            .rankType(rankType)
            .rewardType(rewardType)
            .rewardAmount(rewardAmount)
            .rewardDescription(rewardDescription)
            .isClaimed(false)
            .expiryDate(expiryDate)
            .build();

        // 보상 정보 저장
        GamificationReward savedReward = gamificationRewardPort.saveReward(reward);

        // 보상 생성 이벤트 발행
        publishRewardCreatedEvent(savedReward);

        return savedReward;
    }

    /**
     * 사용자 보상 내역 조회
     *
     * @param userId 사용자 ID
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getUserRewards(Long userId) {
        return gamificationRewardPort.findByUserId(userId);
    }

    /**
     * 사용자 보상 유형별 내역 조회
     *
     * @param userId     사용자 ID
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getUserRewardsByType(Long userId, RewardType rewardType) {
        return gamificationRewardPort.findByUserIdAndRewardType(userId, rewardType);
    }

    /**
     * 사용자 랭크 타입별 보상 내역 조회
     *
     * @param userId   사용자 ID
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getUserRewardsByRankType(Long userId, RankType rankType) {
        return gamificationRewardPort.findByUserIdAndRankType(userId, rankType);
    }

    /**
     * 보상 지급 처리
     *
     * @param rewardId 보상 ID
     * @return 업데이트된 보상 정보
     */
    @Override
    @Transactional
    public GamificationReward claimReward(Long rewardId) {
        // 보상 정보 조회
        GamificationReward reward = gamificationRewardPort.findById(rewardId);

        // 보상이 이미 지급되었는지 확인
        if (reward.getIsClaimed()) {
            throw new IllegalStateException("Reward already claimed");
        }

        // 보상이 만료되었는지 확인
        if (reward.isExpired()) {
            throw new IllegalStateException("Reward has expired");
        }

        // 보상 지급 처리
        reward.claim();

        // 보상 정보 저장
        GamificationReward updatedReward = gamificationRewardPort.saveReward(reward);

        // 보상 지급 이벤트 발행
        publishRewardClaimedEvent(updatedReward);

        return updatedReward;
    }

    /**
     * 만료된 보상 내역 조회
     *
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getExpiredRewards() {
        return gamificationRewardPort.findExpiredRewards(LocalDateTime.now());
    }

    /**
     * 특정 랭크 타입의 보상 내역 조회
     *
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getRewardsByRankType(RankType rankType) {
        return gamificationRewardPort.findByRankType(rankType);
    }

    /**
     * 특정 보상 유형의 보상 내역 조회
     *
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getRewardsByType(RewardType rewardType) {
        return gamificationRewardPort.findByRewardType(rewardType);
    }

    /**
     * 아직 지급되지 않은 보상 내역 조회
     *
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> getUnclaimedRewards() {
        return gamificationRewardPort.findByIsClaimedFalse();
    }

    /**
     * 랭크 변경에 따른 보상 생성
     *
     * @param userId           사용자 ID
     * @param previousRankType 이전 랭크 타입
     * @param newRankType      새 랭크 타입
     * @return 생성된 보상 정보 목록
     */
    @Override
    @Transactional
    public List<GamificationReward> createRewardsForRankChange(Long userId, RankType previousRankType, RankType newRankType) {
        // 랭크가 상승한 경우에만 보상 생성
        if (newRankType.ordinal() <= previousRankType.ordinal()) {
            return List.of();
        }

        List<GamificationReward> rewards = new ArrayList<>();

        // 랭크별 보상 생성
        switch (newRankType) {
            case SILVER:
                // 실버 이상 등급 도달 시 보너스 포인트 지급
                rewards.add(createReward(
                    userId,
                    newRankType,
                    RewardType.BONUS_POINTS,
                    100,
                    "실버 등급 달성 보너스 포인트",
                    LocalDateTime.now().plusDays(30)
                ));
                break;

            case GOLD:
                // 골드 등급 이상 사용자는 매주 추가 경험치 제공
                rewards.add(createReward(
                    userId,
                    newRankType,
                    RewardType.WEEKLY_EXPERIENCE,
                    50,
                    "골드 등급 주간 추가 경험치",
                    LocalDateTime.now().plusDays(7)
                ));
                break;

            case PLATINUM:
                // 플래티넘 이상 사용자는 커뮤니티 이벤트 초대권 제공
                rewards.add(createReward(
                    userId,
                    newRankType,
                    RewardType.EVENT_INVITATION,
                    1,
                    "플래티넘 등급 커뮤니티 이벤트 초대권",
                    LocalDateTime.now().plusDays(60)
                ));
                break;

            case DIAMOND:
                // 다이아 등급 이상 사용자는 관리자 추천 피드에 노출
                rewards.add(createReward(
                    userId,
                    newRankType,
                    RewardType.ADMIN_RECOMMENDATION,
                    1,
                    "다이아 등급 관리자 추천 피드 노출",
                    LocalDateTime.now().plusDays(90)
                ));
                break;

            default:
                break;
        }

        // 보상 정보 저장
        return gamificationRewardPort.saveAllRewards(rewards);
    }

    /**
     * 보상 생성 이벤트 발행
     *
     * @param reward 보상 정보
     */
    private void publishRewardCreatedEvent(GamificationReward reward) {
        // 보상 생성 이벤트 발행 로직
        // eventPublisher.publishEvent(new RewardCreatedEvent(reward));
    }

    /**
     * 보상 지급 이벤트 발행
     *
     * @param reward 보상 정보
     */
    private void publishRewardClaimedEvent(GamificationReward reward) {
        // 보상 지급 이벤트 발행 로직
        // eventPublisher.publishEvent(new RewardClaimedEvent(reward));
    }
} 