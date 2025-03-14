package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.RankingBadge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 랭킹 뱃지 리포지토리 인터페이스
 */
@Repository
public interface RankingBadgeRepository extends JpaRepository<RankingBadge, Long> {

    /**
     * 요구사항 타입과 임계값 이하인 배지 목록 조회
     * 
     * @param requirementType 요구사항 타입
     * @param thresholdValue 임계값
     * @return 조건에 맞는 배지 목록
     */
    List<RankingBadge> findByRequirementTypeAndThresholdValueLessThanEqual(
            @Param("requirementType") String requirementType, 
            @Param("thresholdValue") Integer thresholdValue);

    /**
     * 가장 많이 획득된 뱃지 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 뱃지 타입과 획득 횟수 목록
     */
    @Query("SELECT b.badgeType, COUNT(b) FROM RankingBadge b " +
           "GROUP BY b.badgeType " +
           "ORDER BY COUNT(b) DESC")
    List<Object[]> findMostAcquiredBadges(Pageable pageable);

    /**
     * 뱃지 타입으로 획득 횟수 조회
     *
     * @param badgeType 뱃지 타입
     * @return 획득 횟수
     */
    Long countByBadgeType(String badgeType);
} 