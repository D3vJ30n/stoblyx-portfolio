package com.j30n.stoblyx.adapter.out.persistence.ranking;

import com.j30n.stoblyx.application.port.out.ranking.RankingUserScorePort;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import com.j30n.stoblyx.domain.repository.RankingUserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 점수 관련 아웃 어댑터 구현체
 */
@Component
public class RankingUserScoreAdapter implements RankingUserScorePort {

    @Autowired
    private RankingUserScoreRepository rankingUserScoreRepository;

    /**
     * 사용자 점수 정보 저장
     *
     * @param userScore 점수 정보
     * @return 저장된 점수 정보
     */
    @Override
    public RankingUserScore saveUserScore(RankingUserScore userScore) {
        return rankingUserScoreRepository.save(userScore);
    }

    /**
     * 사용자 점수 정보 목록 저장
     *
     * @param userScores 점수 정보 목록
     * @return 저장된 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> saveAllUserScores(List<RankingUserScore> userScores) {
        return rankingUserScoreRepository.saveAll(userScores);
    }

    /**
     * 사용자 ID로 점수 정보 조회
     *
     * @param userId 사용자 ID
     * @return 점수 정보
     */
    @Override
    public Optional<RankingUserScore> findByUserId(Long userId) {
        return rankingUserScoreRepository.findByUserId(userId);
    }

    /**
     * 랭크 타입으로 사용자 점수 정보 목록 조회
     *
     * @param rankType 랭크 타입
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> findByRankType(RankType rankType) {
        return rankingUserScoreRepository.findByRankType(rankType);
    }

    /**
     * 점수 내림차순으로 상위 N명의 사용자 점수 정보 조회
     *
     * @param limit 조회할 사용자 수
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> findTopUsersByScore(int limit) {
        return rankingUserScoreRepository.findTopUsersByScore(PageRequest.of(0, limit));
    }

    /**
     * 특정 기간 동안 활동이 없는 사용자 점수 정보 조회
     *
     * @param date 기준 일시
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> findInactiveUsersSince(LocalDateTime date) {
        return rankingUserScoreRepository.findInactiveUsersSince(date);
    }

    /**
     * 급격한 점수 상승이 있는 사용자 점수 정보 조회
     *
     * @param threshold 점수 상승 임계값
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> findUsersWithSuspiciousScoreIncrease(Integer threshold) {
        return rankingUserScoreRepository.findUsersWithSuspiciousScoreIncrease(threshold);
    }

    /**
     * 계정이 정지된 사용자 점수 정보 조회
     *
     * @return 점수 정보 목록
     */
    @Override
    public List<RankingUserScore> findByAccountSuspendedTrue() {
        return rankingUserScoreRepository.findByAccountSuspendedTrue();
    }
} 