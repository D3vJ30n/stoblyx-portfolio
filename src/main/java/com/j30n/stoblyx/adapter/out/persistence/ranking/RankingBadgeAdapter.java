package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.RankingBadgePort;
import com.j30n.stoblyx.domain.enums.BadgeRarity;
import com.j30n.stoblyx.domain.model.RankingBadge;
import com.j30n.stoblyx.domain.repository.RankingBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 랭킹 뱃지 관련 아웃 어댑터 구현체
 */
@Component
public class RankingBadgeAdapter implements RankingBadgePort {

    @Autowired
    private RankingBadgeRepository rankingBadgeRepository;

    /**
     * 뱃지 정보 저장
     *
     * @param badge 뱃지 정보
     * @return 저장된 뱃지 정보
     */
    @Override
    public RankingBadge saveBadge(RankingBadge badge) {
        return rankingBadgeRepository.save(badge);
    }

    /**
     * 뱃지 정보 목록 저장
     *
     * @param badges 뱃지 정보 목록
     * @return 저장된 뱃지 정보 목록
     */
    @Override
    public List<RankingBadge> saveAllBadges(List<RankingBadge> badges) {
        return rankingBadgeRepository.saveAll(badges);
    }

    /**
     * 뱃지 ID로 뱃지 정보 조회
     *
     * @param badgeId 뱃지 ID
     * @return 뱃지 정보
     */
    @Override
    public Optional<RankingBadge> findById(Long badgeId) {
        return rankingBadgeRepository.findById(badgeId);
    }

    /**
     * 사용자 ID로 뱃지 정보 목록 조회
     *
     * @param userId 사용자 ID
     * @return 뱃지 정보 목록
     */
    @Override
    public List<RankingBadge> findByUserId(Long userId) {
        return rankingBadgeRepository.findByUserId(userId);
    }

    /**
     * 뱃지 코드로 뱃지 정보 조회
     *
     * @param badgeCode 뱃지 코드
     * @return 뱃지 정보
     */
    @Override
    public Optional<RankingBadge> findByBadgeCode(String badgeCode) {
        return rankingBadgeRepository.findByBadgeCode(badgeCode);
    }

    /**
     * 사용자 ID와 뱃지 코드로 뱃지 정보 조회
     *
     * @param userId    사용자 ID
     * @param badgeCode 뱃지 코드
     * @return 뱃지 정보
     */
    @Override
    public Optional<RankingBadge> findByUserIdAndBadgeCode(Long userId, String badgeCode) {
        return rankingBadgeRepository.findByUserIdAndBadgeCode(userId, badgeCode);
    }

    /**
     * 뱃지 희귀도로 뱃지 정보 목록 조회
     *
     * @param rarity 뱃지 희귀도
     * @return 뱃지 정보 목록
     */
    @Override
    public List<RankingBadge> findByRarity(BadgeRarity rarity) {
        return rankingBadgeRepository.findByRarity(rarity);
    }

    /**
     * 특정 기간 내에 획득한 뱃지 정보 목록 조회
     *
     * @param startDate 시작 일시
     * @param endDate   종료 일시
     * @return 뱃지 정보 목록
     */
    @Override
    public List<RankingBadge> findByAcquiredAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return rankingBadgeRepository.findByAcquiredAtBetween(startDate, endDate);
    }

    /**
     * 가장 많이 획득한 뱃지 정보 목록 조회
     *
     * @param limit 조회할 뱃지 수
     * @return 뱃지 정보 목록
     */
    @Override
    public List<Object[]> findMostAcquiredBadges(int limit) {
        return rankingBadgeRepository.findMostAcquiredBadges(PageRequest.of(0, limit));
    }

    /**
     * 특정 뱃지를 획득한 사용자 수 조회
     *
     * @param badgeCode 뱃지 코드
     * @return 사용자 수
     */
    @Override
    public Long countByBadgeCode(String badgeCode) {
        return rankingBadgeRepository.countByBadgeCode(badgeCode);
    }

    /**
     * 특정 사용자의 뱃지 획득률 조회
     *
     * @param userId      사용자 ID
     * @param totalBadges 전체 뱃지 수
     * @return 뱃지 획득률
     */
    @Override
    public Double calculateUserBadgeRate(Long userId, int totalBadges) {
        Long acquiredBadges = rankingBadgeRepository.countByUserId(userId);
        return (double) acquiredBadges / totalBadges * 100;
    }
} 