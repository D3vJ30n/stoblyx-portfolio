package com.j30n.stoblyx.application.port.in.ranking;

import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;

import java.util.List;

/**
 * 사용자 점수 관련 유스케이스 인터페이스
 */
public interface RankingUserScoreUseCase {

    /**
     * 사용자 점수 정보 조회
     *
     * @param userId 사용자 ID
     * @return 점수 정보
     */
    RankingUserScore getUserScore(Long userId);

    /**
     * 사용자 점수 정보 생성 또는 업데이트
     *
     * @param userId        사용자 ID
     * @param activityScore 활동 점수
     * @return 업데이트된 점수 정보
     */
    RankingUserScore updateUserScore(Long userId, int activityScore);

    /**
     * 사용자 신고 처리
     *
     * @param userId              사용자 ID
     * @param suspensionThreshold 계정 정지 임계값
     * @return 업데이트된 점수 정보
     */
    RankingUserScore reportUser(Long userId, int suspensionThreshold);

    /**
     * 비활동 사용자 점수 감소 처리
     *
     * @param inactivityPeriod 비활동 기간 (일)
     * @param decayFactor      감소 계수
     * @return 업데이트된 점수 정보 목록
     */
    List<RankingUserScore> decayInactiveUserScores(int inactivityPeriod, double decayFactor);

    /**
     * 특정 랭크 타입의 사용자 목록 조회
     *
     * @param rankType 랭크 타입
     * @return 점수 정보 목록
     */
    List<RankingUserScore> getUsersByRankType(RankType rankType);

    /**
     * 상위 N명의 사용자 점수 정보 조회
     *
     * @param limit 조회할 사용자 수
     * @return 점수 정보 목록
     */
    List<RankingUserScore> getTopUsers(int limit);

    /**
     * 급격한 점수 상승이 있는 사용자 목록 조회
     *
     * @param threshold 점수 상승 임계값
     * @return 점수 정보 목록
     */
    List<RankingUserScore> getUsersWithSuspiciousScoreIncrease(int threshold);

    /**
     * 계정이 정지된 사용자 목록 조회
     *
     * @return 점수 정보 목록
     */
    List<RankingUserScore> getSuspendedUsers();
}