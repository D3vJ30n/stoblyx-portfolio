package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.RankingAchievementPort;
import com.j30n.stoblyx.domain.enums.AchievementStatus;
import com.j30n.stoblyx.domain.model.RankingAchievement;
import com.j30n.stoblyx.domain.repository.RankingAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RankingAchievementRepository rankingAchievementRepository;

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
     * 사용자 ID와 업적 상태로 업적 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @param status 업적 상태
     * @return 업적 정보 목록
     */
    @Override
    public List<RankingAchievement> findByUserIdAndStatus(Long userId, AchievementStatus status) {
        return rankingAchievementRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * 업적 코드로 업적 정보 조회
     *
     * @param achievementCode 업적 코드
     * @return 업적 정보
     */
    @Override
    public Optional<RankingAchievement> findByAchievementCode(String achievementCode) {
        return rankingAchievementRepository.findByAchievementCode(achievementCode);
    }

    /**
     * 사용자 ID와 업적 코드로 업적 정보 조회
     *
     * @param userId          사용자 ID
     * @param achievementCode 업적 코드
     * @return 업적 정보
     */
    @Override
    public Optional<RankingAchievement> findByUserIdAndAchievementCode(Long userId, String achievementCode) {
        return rankingAchievementRepository.findByUserIdAndAchievementCode(userId, achievementCode);
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
     * 특정 업적을 획득한 사용자 수 조회
     *
     * @param achievementCode 업적 코드
     * @return 사용자 수
     */
    @Override
    public Long countByAchievementCode(String achievementCode) {
        return rankingAchievementRepository.countByAchievementCode(achievementCode);
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
        Long completedAchievements = rankingAchievementRepository.countByUserIdAndStatus(userId, AchievementStatus.COMPLETED);
        return (double) completedAchievements / totalAchievements * 100;
    }
} 