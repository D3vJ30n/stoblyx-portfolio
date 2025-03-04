package com.j30n.stoblyx.application.port.out.ranking;

import com.j30n.stoblyx.domain.enums.AchievementStatus;
import com.j30n.stoblyx.domain.model.RankingAchievement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 랭킹 업적 관련 포트 인터페이스
 */
public interface RankingAchievementPort {

    /**
     * 업적 정보 저장
     *
     * @param achievement 업적 정보
     * @return 저장된 업적 정보
     */
    RankingAchievement saveAchievement(RankingAchievement achievement);

    /**
     * 업적 정보 목록 저장
     *
     * @param achievements 업적 정보 목록
     * @return 저장된 업적 정보 목록
     */
    List<RankingAchievement> saveAllAchievements(List<RankingAchievement> achievements);

    /**
     * ID로 업적 정보 조회
     *
     * @param achievementId 업적 ID
     * @return 업적 정보
     */
    Optional<RankingAchievement> findById(Long achievementId);

    /**
     * 사용자 ID로 업적 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByUserId(Long userId);

    /**
     * 사용자 ID와 상태로 업적 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @param status 업적 상태
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByUserIdAndStatus(Long userId, AchievementStatus status);

    /**
     * 업적 코드로 업적 정보 조회
     *
     * @param achievementCode 업적 코드
     * @return 업적 정보
     */
    Optional<RankingAchievement> findByAchievementCode(String achievementCode);

    /**
     * 사용자 ID와 업적 코드로 업적 정보 조회
     *
     * @param userId          사용자 ID
     * @param achievementCode 업적 코드
     * @return 업적 정보
     */
    Optional<RankingAchievement> findByUserIdAndAchievementCode(Long userId, String achievementCode);

    /**
     * 달성 일시 범위로 업적 정보 목록 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 업적 정보 목록
     */
    List<RankingAchievement> findByAchievedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 가장 많이 달성된 업적 목록 조회
     *
     * @param limit 조회 개수
     * @return 업적 코드와 달성 횟수 목록
     */
    List<Object[]> findMostAchievedAchievements(int limit);

    /**
     * 업적 코드로 달성 횟수 조회
     *
     * @param achievementCode 업적 코드
     * @return 달성 횟수
     */
    Long countByAchievementCode(String achievementCode);

    /**
     * 사용자의 업적 달성률 계산
     *
     * @param userId            사용자 ID
     * @param totalAchievements 전체 업적 수
     * @return 달성률 (%)
     */
    Double calculateUserAchievementRate(Long userId, int totalAchievements);
} 