package com.j30n.stoblyx.adapter.out.persistence.admin;

import com.j30n.stoblyx.application.port.out.admin.AdminGamificationPort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.enums.RewardType;
import com.j30n.stoblyx.domain.model.GamificationReward;
import com.j30n.stoblyx.domain.repository.GamificationRewardRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 관리자의 게이미피케이션 시스템 관리를 위한 아웃 어댑터 구현체
 */
@Component
public class AdminGamificationAdapter implements AdminGamificationPort {

    private final GamificationRewardRepository gamificationRewardRepository;

    public AdminGamificationAdapter(GamificationRewardRepository gamificationRewardRepository) {
        this.gamificationRewardRepository = gamificationRewardRepository;
    }

    /**
     * 특정 사용자의 보상 내역 조회
     *
     * @param userId 사용자 ID
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> findRewardsByUserId(Long userId) {
        return gamificationRewardRepository.findByUserId(userId);
    }

    /**
     * 특정 보상 유형의 보상 내역 조회
     *
     * @param rewardType 보상 유형
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> findRewardsByType(RewardType rewardType) {
        return gamificationRewardRepository.findByRewardType(rewardType);
    }

    /**
     * 특정 랭크 타입의 보상 내역 조회
     *
     * @param rankType 랭크 타입
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> findRewardsByRankType(RankType rankType) {
        // 현재 GamificationReward 엔티티에는 rankType 필드가 없음
        // 임시로 빈 리스트 반환
        return List.of();
    }

    /**
     * 특정 기간 내 생성된 보상 내역 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> findRewardsByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return gamificationRewardRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * 만료된 보상 내역 조회
     *
     * @param currentDate 현재 일시
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> findExpiredRewards(LocalDateTime currentDate) {
        return gamificationRewardRepository.findExpiredRewards(currentDate);
    }

    /**
     * 아직 지급되지 않은 보상 내역 조회
     *
     * @return 보상 내역 목록
     */
    @Override
    public List<GamificationReward> findUnclaimedRewards() {
        return gamificationRewardRepository.findByIsClaimedFalse();
    }

    /**
     * 수동으로 보상 생성
     *
     * @param userId       사용자 ID
     * @param rewardType   보상 유형
     * @param rewardAmount 보상 양
     * @param description  보상 설명
     * @param expiryDate   만료 일시
     * @return 생성된 보상 내역
     */
    @Override
    @Transactional
    public GamificationReward createReward(Long userId, RewardType rewardType, Integer rewardAmount, String description, LocalDateTime expiryDate) {
        GamificationReward reward = GamificationReward.builder()
            .userId(userId)
            .rewardType(rewardType)
            .points(rewardAmount)
            .description(description)
            .referenceType(null)
            .referenceId(null)
            .isClaimed(false)
            .expiryDate(expiryDate)
            .build();

        return gamificationRewardRepository.save(reward);
    }

    /**
     * 보상 내역 수정
     *
     * @param rewardId     보상 ID
     * @param rewardAmount 보상 양
     * @param description  보상 설명
     * @param expiryDate   만료 일시
     * @return 수정된 보상 내역
     */
    @Override
    @Transactional
    public GamificationReward updateReward(Long rewardId, Integer rewardAmount, String description, LocalDateTime expiryDate) {
        GamificationReward reward = gamificationRewardRepository.findById(rewardId)
            .orElseThrow(() -> new IllegalArgumentException("보상 내역을 찾을 수 없습니다: " + rewardId));

        // 새로운 객체로 생성하고 원래 값 유지
        GamificationReward updatedReward = GamificationReward.builder()
            .id(reward.getId())
            .userId(reward.getUserId())
            .rewardType(reward.getRewardType())
            .points(rewardAmount != null ? rewardAmount : reward.getPoints())
            .description(description != null ? description : reward.getDescription())
            .referenceId(reward.getReferenceId())
            .referenceType(reward.getReferenceType())
            .isClaimed(reward.isClaimed())
            .expiryDate(expiryDate != null ? expiryDate : reward.getExpiryDate())
            .build();

        return gamificationRewardRepository.save(updatedReward);
    }

    /**
     * 보상 내역 삭제
     *
     * @param rewardId 보상 ID
     * @return 삭제 성공 여부
     */
    @Override
    @Transactional
    public boolean deleteReward(Long rewardId) {
        if (!gamificationRewardRepository.existsById(rewardId)) {
            return false;
        }

        gamificationRewardRepository.deleteById(rewardId);
        return true;
    }

    /**
     * 보상 지급 처리
     *
     * @param rewardId 보상 ID
     * @return 지급 처리된 보상 내역
     */
    @Override
    @Transactional
    public GamificationReward claimReward(Long rewardId) {
        GamificationReward reward = gamificationRewardRepository.findById(rewardId)
            .orElseThrow(() -> new IllegalArgumentException("보상 내역을 찾을 수 없습니다: " + rewardId));

        if (reward.isClaimed()) {
            throw new IllegalStateException("이미 지급된 보상입니다: " + rewardId);
        }

        if (reward.isExpired()) {
            throw new IllegalStateException("만료된 보상입니다: " + rewardId);
        }

        reward.claim();

        return gamificationRewardRepository.save(reward);
    }

    /**
     * 보상 유형별 통계 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 보상 유형별 지급 횟수 맵
     */
    @Override
    public Map<RewardType, Long> getRewardTypeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<GamificationReward> rewards = gamificationRewardRepository.findByCreatedAtBetween(startDate, endDate);

        return rewards.stream()
            .collect(Collectors.groupingBy(
                GamificationReward::getRewardType,
                () -> new EnumMap<>(RewardType.class),
                Collectors.counting()
            ));
    }

    /**
     * 랭크 타입별 보상 통계 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 랭크 타입별 보상 지급 횟수 맵
     */
    @Override
    public Map<RankType, Long> getRankTypeRewardStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // 현재 GamificationReward 엔티티에는 rankType 필드가 없음
        // 임시로 빈 맵 반환
        return new EnumMap<>(RankType.class);
    }

    /**
     * 게이미피케이션 시스템 설정 업데이트
     *
     * @param settingKey   설정 키
     * @param settingValue 설정 값
     * @return 업데이트 성공 여부
     */
    @Override
    public boolean updateGamificationSystemSetting(String settingKey, String settingValue) {
        // 실제 구현에서는 설정 저장소를 사용하여 설정 업데이트
        // 여기서는 간단히 성공 반환
        return true;
    }
} 