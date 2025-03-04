package com.j30n.stoblyx.adapter.out.persistence.admin;

import com.j30n.stoblyx.application.port.out.admin.AdminRankingPort;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import com.j30n.stoblyx.domain.repository.RankingUserActivityRepository;
import com.j30n.stoblyx.domain.repository.RankingUserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 관리자의 랭킹 시스템 관리를 위한 아웃 어댑터 구현체
 */
@Component
public class AdminRankingAdapter implements AdminRankingPort {

    @Autowired
    private RankingUserScoreRepository rankingUserScoreRepository;

    @Autowired
    private RankingUserActivityRepository rankingUserActivityRepository;

    /**
     * 의심스러운 활동이 있는 사용자 목록 조회
     * 
     * @param threshold 점수 변화 임계값
     * @return 의심스러운 활동이 있는 사용자 목록
     */
    @Override
    public List<RankingUserScore> findUsersWithSuspiciousActivity(int threshold) {
        return rankingUserScoreRepository.findUsersWithSuspiciousScoreIncrease(threshold);
    }

    /**
     * 특정 기간 내 비정상적인 활동 패턴 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param activityThreshold 활동 횟수 임계값
     * @return 비정상적인 활동 패턴 목록
     */
    @Override
    public List<RankingUserActivity> findAbnormalActivityPatterns(LocalDateTime startDate, LocalDateTime endDate, int activityThreshold) {
        // 특정 기간 내 사용자별 활동 횟수를 계산하여 임계값을 초과하는 활동 패턴 조회
        // 실제 구현에서는 더 복잡한 로직이 필요할 수 있음
        List<RankingUserActivity> allActivities = rankingUserActivityRepository.findByCreatedAtBetween(startDate, endDate);
        
        // 사용자별 활동 횟수 계산
        Map<Long, Long> userActivityCount = allActivities.stream()
                .collect(Collectors.groupingBy(RankingUserActivity::getUserId, Collectors.counting()));
        
        // 임계값을 초과하는 사용자의 활동만 필터링
        List<Long> suspiciousUsers = userActivityCount.entrySet().stream()
                .filter(entry -> entry.getValue() > activityThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // 의심스러운 사용자의 활동만 반환
        return allActivities.stream()
                .filter(activity -> suspiciousUsers.contains(activity.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 IP 주소의 활동 내역 조회
     * 
     * @param ipAddress IP 주소
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 내역 목록
     */
    @Override
    public List<RankingUserActivity> findActivitiesByIpAddress(String ipAddress, LocalDateTime startDate, LocalDateTime endDate) {
        return rankingUserActivityRepository.findByIpAddressAndCreatedAtBetween(ipAddress, startDate, endDate);
    }

    /**
     * 사용자 점수 수동 조정
     * 
     * @param userId 사용자 ID
     * @param scoreAdjustment 점수 조정값
     * @param reason 조정 사유
     * @return 조정된 사용자 점수 정보
     */
    @Override
    @Transactional
    public RankingUserScore adjustUserScore(Long userId, int scoreAdjustment, String reason) {
        RankingUserScore userScore = rankingUserScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 점수 정보를 찾을 수 없습니다: " + userId));
        
        // 이전 점수 저장
        userScore.setPreviousScore(userScore.getCurrentScore());
        
        // 점수 조정
        int newScore = userScore.getCurrentScore() + scoreAdjustment;
        userScore.setCurrentScore(newScore);
        
        // 랭크 타입 업데이트
        userScore.setRankType(RankType.fromScore(newScore));
        
        // 관리자 조정 활동 기록
        RankingUserActivity activity = new RankingUserActivity();
        activity.setUserId(userId);
        activity.setTargetId(userId);
        activity.setTargetType("USER_SCORE");
        activity.setActivityType(ActivityType.ADMIN_ADJUSTMENT);
        activity.setScoreChange(scoreAdjustment);
        activity.setIpAddress("ADMIN");
        activity.setCreatedAt(LocalDateTime.now());
        
        // 활동 기록 저장
        rankingUserActivityRepository.save(activity);
        
        // 수정된 점수 정보 저장 및 반환
        return rankingUserScoreRepository.save(userScore);
    }

    /**
     * 사용자 계정 정지 처리
     * 
     * @param userId 사용자 ID
     * @param reason 정지 사유
     * @return 정지된 사용자 점수 정보
     */
    @Override
    @Transactional
    public RankingUserScore suspendUserAccount(Long userId, String reason) {
        RankingUserScore userScore = rankingUserScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 점수 정보를 찾을 수 없습니다: " + userId));
        
        // 계정 정지 처리
        userScore.setAccountSuspended(true);
        
        // 관리자 정지 활동 기록
        RankingUserActivity activity = new RankingUserActivity();
        activity.setUserId(userId);
        activity.setTargetId(userId);
        activity.setTargetType("USER_ACCOUNT");
        activity.setActivityType(ActivityType.ADMIN_SUSPENSION);
        activity.setScoreChange(0);
        activity.setIpAddress("ADMIN");
        activity.setCreatedAt(LocalDateTime.now());
        
        // 활동 기록 저장
        rankingUserActivityRepository.save(activity);
        
        // 수정된 점수 정보 저장 및 반환
        return rankingUserScoreRepository.save(userScore);
    }

    /**
     * 사용자 계정 정지 해제
     * 
     * @param userId 사용자 ID
     * @return 정지 해제된 사용자 점수 정보
     */
    @Override
    @Transactional
    public RankingUserScore unsuspendUserAccount(Long userId) {
        RankingUserScore userScore = rankingUserScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 점수 정보를 찾을 수 없습니다: " + userId));
        
        // 계정 정지 해제
        userScore.setAccountSuspended(false);
        
        // 관리자 정지 해제 활동 기록
        RankingUserActivity activity = new RankingUserActivity();
        activity.setUserId(userId);
        activity.setTargetId(userId);
        activity.setTargetType("USER_ACCOUNT");
        activity.setActivityType(ActivityType.ADMIN_UNSUSPENSION);
        activity.setScoreChange(0);
        activity.setIpAddress("ADMIN");
        activity.setCreatedAt(LocalDateTime.now());
        
        // 활동 기록 저장
        rankingUserActivityRepository.save(activity);
        
        // 수정된 점수 정보 저장 및 반환
        return rankingUserScoreRepository.save(userScore);
    }

    /**
     * 랭크 타입별 사용자 분포 통계 조회
     * 
     * @return 랭크 타입별 사용자 수 맵
     */
    @Override
    public Map<RankType, Long> getRankDistributionStatistics() {
        Map<RankType, Long> result = new HashMap<>();
        
        for (RankType rankType : RankType.values()) {
            long count = rankingUserScoreRepository.countByRankType(rankType);
            result.put(rankType, count);
        }
        
        return result;
    }

    /**
     * 활동 유형별 발생 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 유형별 발생 횟수 맵
     */
    @Override
    public Map<ActivityType, Long> getActivityTypeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<RankingUserActivity> activities = rankingUserActivityRepository.findByCreatedAtBetween(startDate, endDate);
        
        return activities.stream()
                .collect(Collectors.groupingBy(RankingUserActivity::getActivityType, Collectors.counting()));
    }

    /**
     * 시간대별 활동 통계 조회
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 시간대별 활동 횟수 맵
     */
    @Override
    public Map<Integer, Long> getActivityByHourStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<RankingUserActivity> activities = rankingUserActivityRepository.findByCreatedAtBetween(startDate, endDate);
        
        return activities.stream()
                .collect(Collectors.groupingBy(activity -> activity.getCreatedAt().getHour(), Collectors.counting()));
    }

    /**
     * 랭킹 시스템 설정 업데이트
     * 
     * @param settingKey 설정 키
     * @param settingValue 설정 값
     * @return 업데이트 성공 여부
     */
    @Override
    public boolean updateRankingSystemSetting(String settingKey, String settingValue) {
        // 실제 구현에서는 설정 저장소를 사용하여 설정 업데이트
        // 여기서는 간단히 성공 반환
        return true;
    }
} 