package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.ranking.RankingBadgePort;
import com.j30n.stoblyx.domain.model.RankingBadge;
import com.j30n.stoblyx.domain.repository.RankingBadgeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 랭킹 뱃지 관련 아웃 어댑터 구현체
 */
@Component
public class RankingBadgeAdapter implements RankingBadgePort {

    private final RankingBadgeRepository rankingBadgeRepository;
    
    /**
     * 생성자 주입
     * 
     * @param rankingBadgeRepository 랭킹 뱃지 리포지토리
     */
    public RankingBadgeAdapter(RankingBadgeRepository rankingBadgeRepository) {
        this.rankingBadgeRepository = rankingBadgeRepository;
    }

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
     * ID로 뱃지 정보 조회
     *
     * @param badgeId 뱃지 ID
     * @return 뱃지 정보
     */
    @Override
    public Optional<RankingBadge> findById(Long badgeId) {
        return rankingBadgeRepository.findById(badgeId);
    }
    
    /**
     * 모든 배지 목록 조회
     *
     * @return 모든 배지 목록
     */
    @Override
    public List<RankingBadge> findAll() {
        return rankingBadgeRepository.findAll();
    }
    
    /**
     * 특정 요구사항 타입과 임계값 이하의 배지 목록 조회
     *
     * @param requirementType 요구사항 타입
     * @param thresholdValue 임계값
     * @return 조건에 맞는 배지 목록
     */
    @Override
    public List<RankingBadge> findByRequirementTypeAndThresholdValueLessThanEqual(String requirementType, Integer thresholdValue) {
        return rankingBadgeRepository.findByRequirementTypeAndThresholdValueLessThanEqual(requirementType, thresholdValue);
    }

    /**
     * 가장 많이 획득된 뱃지 목록 조회
     *
     * @param limit 조회 개수
     * @return 뱃지 타입과 획득 횟수 목록
     */
    @Override
    public List<Object[]> findMostAcquiredBadges(int limit) {
        return rankingBadgeRepository.findMostAcquiredBadges(PageRequest.of(0, limit));
    }

    /**
     * 뱃지 타입으로 획득 횟수 조회
     *
     * @param badgeType 뱃지 타입
     * @return 획득 횟수
     */
    @Override
    public Long countByBadgeType(String badgeType) {
        return rankingBadgeRepository.countByBadgeType(badgeType);
    }
} 