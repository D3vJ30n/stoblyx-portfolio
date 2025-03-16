package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 점수 정보 레포지토리
 */
@Repository
public interface RankingUserScoreRepository extends JpaRepository<RankingUserScore, Long> {

    /**
     * 사용자 ID로 점수 정보 조회
     *
     * @param userId 사용자 ID
     * @return 점수 정보
     */
    Optional<RankingUserScore> findByUserId(Long userId);

    /**
     * 랭크 타입으로 사용자 점수 정보 목록 조회
     *
     * @param rankType 랭크 타입
     * @return 점수 정보 목록
     */
    List<RankingUserScore> findByRankType(RankType rankType);

    /**
     * 랭크 타입별 사용자 수 조회
     *
     * @param rankType 랭크 타입
     * @return 사용자 수
     */
    long countByRankType(RankType rankType);

    /**
     * 점수 내림차순으로 상위 N명의 사용자 점수 정보 조회
     *
     * @param pageable 페이징 정보
     * @return 점수 정보 목록
     */
    @Query("SELECT r FROM RankingUserScore r WHERE r.accountSuspended = false ORDER BY r.currentScore DESC")
    List<RankingUserScore> findTopUsersByScore(Pageable pageable);

    /**
     * 특정 기간 동안 활동이 없는 사용자 점수 정보 조회
     *
     * @param date 기준 일시
     * @return 점수 정보 목록
     */
    @Query("SELECT r FROM RankingUserScore r WHERE r.lastActivityDate < :date")
    List<RankingUserScore> findInactiveUsersSince(@Param("date") LocalDateTime date);

    /**
     * 급격한 점수 상승이 있는 사용자 점수 정보 조회
     *
     * @param threshold 점수 상승 임계값
     * @return 점수 정보 목록
     */
    @Query("SELECT r FROM RankingUserScore r WHERE (r.currentScore - r.previousScore) > :threshold")
    List<RankingUserScore> findUsersWithSuspiciousScoreIncrease(@Param("threshold") Integer threshold);

    /**
     * 계정이 정지된 사용자 점수 정보 조회
     *
     * @return 점수 정보 목록
     */
    List<RankingUserScore> findByAccountSuspendedTrue();

    /**
     * 상위 N명의 사용자 조회
     *
     * @param limit 상위 N명의 제한
     * @return 점수 정보 목록
     */
    @Query("SELECT s FROM RankingUserScore s ORDER BY s.currentScore DESC")
    List<RankingUserScore> findTopByOrderByCurrentScoreDesc(int limit);

    /**
     * 최근 점수 변경이 큰 내역 조회
     *
     * @param pageable 페이징 정보
     * @return 점수 변경 내역 목록 (userId, null, rankType, scoreDiff, modifiedAt)
     */
    @Query(value = "SELECT s.userId, null, s.rankType, (s.currentScore - s.previousScore) as scoreDiff, s.modifiedAt " +
        "FROM RankingUserScore s " +
        "WHERE s.previousScore IS NOT NULL AND s.currentScore <> s.previousScore " +
        "ORDER BY s.modifiedAt DESC",
        countQuery = "SELECT COUNT(s) FROM RankingUserScore s WHERE s.previousScore IS NOT NULL AND s.currentScore <> s.previousScore")
    List<Object[]> findRecentRankChanges(Pageable pageable);

    /**
     * 최근 점수 변경이 큰 내역 조회 (제한된 수)
     *
     * @param limit 최근 변경 내역의 제한
     * @return 점수 변경 내역 목록 (userId, null, rankType, scoreDiff, modifiedAt)
     */
    default List<Object[]> findRecentRankChanges(@Param("limit") int limit) {
        return findRecentRankChanges(PageRequest.of(0, limit));
    }
} 