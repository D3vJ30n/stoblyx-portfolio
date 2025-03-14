package com.j30n.stoblyx.application.service.gamification;

import com.j30n.stoblyx.application.port.in.gamification.GamificationRewardUseCase;
import com.j30n.stoblyx.application.port.out.gamification.GamificationRewardPort;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게이미피케이션 보상 관련 서비스 구현체
 */
@Service
public class GamificationRewardService implements GamificationRewardUseCase {

    private final GamificationRewardPort gamificationRewardPort;
    private final GamificationRewardUseCase self;
    
    /**
     * 생성자 주입
     * 
     * @param gamificationRewardPort 게이미피케이션 보상 포트
     * @param self 자기 참조 (트랜잭션 처리를 위한 프록시)
     */
    public GamificationRewardService(GamificationRewardPort gamificationRewardPort, 
                                    @Lazy GamificationRewardUseCase self) {
        this.gamificationRewardPort = gamificationRewardPort;
        this.self = self;
    }

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
    @Override
    @Transactional
    public GamificationReward createReward(Long userId, RewardType rewardType, Integer rewardAmount, String rewardDescription, LocalDateTime expiryDate) {
        // 보상 정보 생성
        GamificationReward reward = GamificationReward.builder()
            .userId(userId)
            .rewardType(rewardType)
            .points(rewardAmount)
            .description(rewardDescription)
            .isClaimed(false)
            .expiryDate(expiryDate)
            .build();

        // 보상 정보 저장 및 반환
        return gamificationRewardPort.saveReward(reward);
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
        if (Boolean.TRUE.equals(reward.getIsClaimed())) {
            throw new IllegalStateException("Reward already claimed");
        }

        // 보상이 만료되었는지 확인
        if (reward.isExpired()) {
            throw new IllegalStateException("Reward has expired");
        }

        // 보상 지급 처리
        reward.claim();

        // 다른 트랜잭션 메서드 호출 시 self 사용
        self.createReward(reward.getUserId(), reward.getRewardType(), 
                         reward.getPoints(), "Claimed reward", null);

        // 보상 정보 저장 및 반환
        return gamificationRewardPort.saveReward(reward);
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
     * 보상 ID로 보상 정보 조회
     *
     * @param rewardId 보상 ID
     * @return 보상 정보
     * @throws IllegalArgumentException 해당 ID의 보상이 없을 경우
     */
    @Override
    public GamificationReward findById(Long rewardId) {
        return gamificationRewardPort.findById(rewardId);
    }
} 