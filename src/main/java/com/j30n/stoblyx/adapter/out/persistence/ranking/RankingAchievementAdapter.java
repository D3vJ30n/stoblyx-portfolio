package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.ranking.RankingAchievementPort;
import com.j30n.stoblyx.domain.model.RankingAchievement;
import com.j30n.stoblyx.domain.repository.RankingAchievementRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 랭킹 업적 관련 아웃 어댑터 구현체
 */
@Component
public class RankingAchievementAdapter implements RankingAchievementPort {

    private final RankingAchievementRepository rankingAchievementRepository;
    
    /**
     * 생성자 주입
     * 
     * @param rankingAchievementRepository 랭킹 업적 리포지토리
     */
    public RankingAchievementAdapter(RankingAchievementRepository rankingAchievementRepository) {
        this.rankingAchievementRepository = rankingAchievementRepository;
    }

    /**
     * 업적 정보 저장
     *
     * @param achievement 업적 정보
     * @return 저장된 업적 정보
     */
    @Override
    public RankingAchievement saveAchievement(RankingAchievement achievement) {
        return rankingAchievementRepository.save(achievement);
    }

    /**
     * 업적 정보 목록 저장
     *
     * @param achievements 업적 정보 목록
     * @return 저장된 업적 정보 목록
     */
    @Override
    public List<RankingAchievement> saveAllAchievements(List<RankingAchievement> achievements) {
        return rankingAchievementRepository.saveAll(achievements);
    }

    /**
     * 업적 ID로 업적 정보 조회
     *
     * @param achievementId 업적 ID
     * @return 업적 정보
     */
    @Override
    public Optional<RankingAchievement> findById(Long achievementId) {
        return rankingAchievementRepository.findById(achievementId);
    }

    /**
     * 사용자 ID로 업적 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @return 업적 정보 목록
     */
    @Override
    public List<RankingAchievement> findByUserId(Long userId) {
        return rankingAchievementRepository.findByUserId(userId);
    }

    /**
     * 뱃지 타입으로 업적 정보 조회
     *
     * @param badgeType 뱃지 타입
     * @return 업적 정보
     */
    @Override
    public Optional<RankingAchievement> findByBadgeType(String badgeType) {
        return rankingAchievementRepository.findByBadgeType(badgeType);
    }

    /**
     * 사용자 ID와 뱃지 타입으로 업적 정보 조회
     *
     * @param userId    사용자 ID
     * @param badgeType 뱃지 타입
     * @return 업적 정보
     */
    @Override
    public Optional<RankingAchievement> findByUserIdAndBadgeType(Long userId, String badgeType) {
        return rankingAchievementRepository.findByUserIdAndBadgeType(userId, badgeType);
    }

    /**
     * 특정 기간 내에 획득한 업적 정보 목록 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 업적 정보 목록
     */
    @Override
    public List<RankingAchievement> findByAchievedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return rankingAchievementRepository.findByAchievedAtBetween(startDate, endDate);
    }

    /**
     * 가장 많이 획득한 업적 정보 목록 조회
     *
     * @param limit 조회할 업적 수
     * @return 업적 정보 목록
     */
    @Override
    public List<Object[]> findMostAchievedAchievements(int limit) {
        return rankingAchievementRepository.findMostAchievedAchievements(PageRequest.of(0, limit));
    }

    /**
     * 특정 뱃지 타입을 획득한 사용자 수 조회
     *
     * @param badgeType 뱃지 타입
     * @return 사용자 수
     */
    @Override
    public Long countByBadgeType(String badgeType) {
        return rankingAchievementRepository.countByBadgeType(badgeType);
    }

    /**
     * 특정 사용자의 업적 달성률 조회
     *
     * @param userId            사용자 ID
     * @param totalAchievements 전체 업적 수
     * @return 업적 달성률
     */
    @Override
    public Double calculateUserAchievementRate(Long userId, int totalAchievements) {
        // 전체 업적 중 사용자가 달성한 업적 비율 계산
        long completedAchievements = rankingAchievementRepository.countByUserId(userId);
        return (double) completedAchievements / totalAchievements * 100;
    }
} 