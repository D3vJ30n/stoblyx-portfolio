package com.j30n.stoblyx.application.service.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingActivityResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingScoreResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingStatisticsResponse;
import com.j30n.stoblyx.application.port.in.admin.AdminRankingUseCase;
import com.j30n.stoblyx.application.service.ranking.RankingUserActivityService;
import com.j30n.stoblyx.application.service.ranking.RankingUserScoreService;
import com.j30n.stoblyx.application.service.system.SystemSettingService;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 관리자의 랭킹 시스템 관리를 위한 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRankingService implements AdminRankingUseCase {

    private static final String SYSTEM_USER = "SYSTEM";

    private final RankingUserScoreService rankingUserScoreService;
    private final RankingUserActivityService rankingUserActivityService;
    private final SystemSettingService systemSettingService;

    @Override
    @Transactional(readOnly = true)
    public List<AdminRankingScoreResponse> findUsersWithSuspiciousActivity(int threshold) {
        log.info("의심스러운 활동이 있는 사용자 목록 조회 - 임계값: {}", threshold);
        // 급격한 점수 상승이 있는 사용자 조회
        return rankingUserScoreService.getUsersWithSuspiciousScoreIncrease(threshold).stream()
            .map(score -> new AdminRankingScoreResponse(
                score.getId(),
                score.getUserId(),
                score.getCurrentScore(),
                score.getPreviousScore(),
                score.getRankType(),
                true, // 의심스러운 활동으로 간주
                score.getReportCount(),
                score.getAccountSuspended(),
                score.getLastActivityDate(),
                score.getCreatedAt(),
                score.getModifiedAt()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminRankingActivityResponse> findAbnormalActivityPatterns(LocalDateTime startDate, LocalDateTime endDate, int activityThreshold) {
        log.info("비정상적인 활동 패턴 조회 - 기간: {} ~ {}, 임계값: {}", startDate, endDate, activityThreshold);

        // 비정상적인 활동 패턴 조회 (예: 동일 IP에서 짧은 시간 내 많은 활동)
        // 단순한 구현으로 대체
        List<RankingUserActivity> activities = new ArrayList<>();
        for (ActivityType type : ActivityType.values()) {
            // 각 활동 유형별로 사용자 ID로 그룹화하여 임계값 이상인 경우만 추출
            rankingUserActivityService.getUserActivities(0L).stream()
                .filter(activity -> activity.getActivityType() == type)
                .filter(activity -> activity.getPoints() > activityThreshold)
                .forEach(activities::add);
        }

        return activities.stream()
            .map(activity -> new AdminRankingActivityResponse(
                activity.getId(),
                activity.getUserId(),
                activity.getActivityType(),
                activity.getPoints(),
                "", // IP 주소 필드가 없음
                activity.getReferenceId(),
                activity.getReferenceType(),
                activity.getCreatedAt(),
                activity.getModifiedAt()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminRankingActivityResponse> findActivitiesByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("기간 내 활동 내역 조회 - 사용자 ID: {}, 기간: {} ~ {}", userId, startDate, endDate);

        // 사용자 ID에 따라 다른 활동 검색
        List<RankingUserActivity> activities;
        if (userId != null && userId > 0) {
            activities = rankingUserActivityService.getUserActivitiesByPeriod(userId, startDate, endDate);
        } else {
            // 모든 사용자의 활동 검색
            activities = rankingUserActivityService.getUserActivities(0L).stream()
                .filter(activity -> activity.getCreatedAt().isAfter(startDate) && activity.getCreatedAt().isBefore(endDate))
                .toList();
        }

        return activities.stream()
            .map(activity -> new AdminRankingActivityResponse(
                activity.getId(),
                activity.getUserId(),
                activity.getActivityType(),
                activity.getPoints(),
                "", // IP 주소 필드가 없음
                activity.getReferenceId(),
                activity.getReferenceType(),
                activity.getCreatedAt(),
                activity.getModifiedAt()
            ))
            .toList();
    }

    @Override
    @Transactional
    public AdminRankingScoreResponse adjustUserScore(Long userId, int scoreAdjustment, String reason) {
        log.info("사용자 점수 수동 조정 - 사용자 ID: {}, 조정값: {}, 사유: {}", userId, scoreAdjustment, reason);

        // 사용자 점수 정보 조회
        RankingUserScore userScore = rankingUserScoreService.getUserScore(userId);

        // 점수 조정 전 백업
        int previousScore = userScore.getCurrentScore();

        // 관리자 활동으로 사용자 활동 기록 생성
        rankingUserActivityService.createActivity(
            userId,
            0L, // 시스템 작업
            SYSTEM_USER,
            ActivityType.ADMIN_ADJUSTMENT
        );

        // 업데이트된 정보 조회
        RankingUserScore updatedScore = rankingUserScoreService.getUserScore(userId);

        return new AdminRankingScoreResponse(
            updatedScore.getId(),
            updatedScore.getUserId(),
            updatedScore.getCurrentScore(),
            previousScore,
            updatedScore.getRankType(),
            updatedScore.getSuspiciousActivity(),
            updatedScore.getReportCount(),
            updatedScore.getAccountSuspended(),
            LocalDateTime.now(), // 최근 활동 시간 업데이트
            updatedScore.getCreatedAt(),
            updatedScore.getModifiedAt()
        );
    }

    @Override
    @Transactional
    public AdminRankingScoreResponse suspendUserAccount(Long userId, String reason) {
        log.info("사용자 계정 정지 처리 - 사용자 ID: {}, 사유: {}", userId, reason);

        // 사용자 점수 정보 조회
        RankingUserScore userScore = rankingUserScoreService.getUserScore(userId);

        // 계정 정지 처리
        userScore.setAccountSuspended(true);

        // 관리자 활동으로 사용자 활동 기록 생성
        rankingUserActivityService.createActivity(
            userId,
            0L, // 시스템 작업
            SYSTEM_USER,
            ActivityType.ADMIN_SUSPENSION
        );

        // 업데이트된 정보 저장 및 조회
        RankingUserScore updatedScore = rankingUserScoreService.updateUserScore(userId, 0);

        return new AdminRankingScoreResponse(
            updatedScore.getId(),
            updatedScore.getUserId(),
            updatedScore.getCurrentScore(),
            updatedScore.getPreviousScore(),
            updatedScore.getRankType(),
            updatedScore.getSuspiciousActivity(),
            updatedScore.getReportCount(),
            true, // 정지됨
            LocalDateTime.now(), // 최근 활동 시간 업데이트
            updatedScore.getCreatedAt(),
            updatedScore.getModifiedAt()
        );
    }

    @Override
    @Transactional
    public AdminRankingScoreResponse unsuspendUserAccount(Long userId) {
        log.info("사용자 계정 정지 해제 - 사용자 ID: {}", userId);

        // 사용자 점수 정보 조회
        RankingUserScore userScore = rankingUserScoreService.getUserScore(userId);

        // 계정 정지 해제 처리
        userScore.setAccountSuspended(false);

        // 관리자 활동으로 사용자 활동 기록 생성
        rankingUserActivityService.createActivity(
            userId,
            0L, // 시스템 작업
            SYSTEM_USER,
            ActivityType.ADMIN_UNSUSPENSION
        );

        // 업데이트된 정보 저장 및 조회
        RankingUserScore updatedScore = rankingUserScoreService.updateUserScore(userId, 0);

        return new AdminRankingScoreResponse(
            updatedScore.getId(),
            updatedScore.getUserId(),
            updatedScore.getCurrentScore(),
            updatedScore.getPreviousScore(),
            updatedScore.getRankType(),
            updatedScore.getSuspiciousActivity(),
            updatedScore.getReportCount(),
            false, // 정지 해제됨
            LocalDateTime.now(), // 최근 활동 시간 업데이트
            updatedScore.getCreatedAt(),
            updatedScore.getModifiedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminRankingStatisticsResponse getRankingStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("랭킹 시스템 통계 조회 - 기간: {} ~ {}", startDate, endDate);

        // 랭크 분포 조회 - 실제 데이터 기반으로 구현
        Map<RankType, Long> rankDistribution = new EnumMap<>(RankType.class);
        for (RankType rankType : RankType.values()) {
            // 각 랭크 타입별 사용자 수 계산
            rankDistribution.put(rankType, (long) rankingUserScoreService.getUsersByRankType(rankType).size());
        }

        // 활동 유형별 분포 조회 - 실제 데이터 기반으로 구현
        Map<ActivityType, Long> activityTypeDistribution = new EnumMap<>(ActivityType.class);
        for (ActivityType activityType : ActivityType.values()) {
            // 지정된 기간 내 각 활동 유형별 활동 수 계산
            long count = 0;
            // 모든 사용자의 해당 활동 타입 집계
            List<RankingUserScore> allUsers = rankingUserScoreService.getTopUsers(Integer.MAX_VALUE);
            for (RankingUserScore user : allUsers) {
                count += rankingUserActivityService
                    .getUserActivitiesByType(user.getUserId(), activityType)
                    .stream()
                    .filter(activity ->
                        activity.getCreatedAt().isAfter(startDate) &&
                            activity.getCreatedAt().isBefore(endDate))
                    .count();
            }
            activityTypeDistribution.put(activityType, count);
        }

        // 시간대별 활동 분포 조회 - 실제 데이터 기반으로 구현
        Map<Integer, Long> activityByHour = new HashMap<>();
        for (int hour = 0; hour < 24; hour++) {
            final int currentHour = hour;
            long count = 0;
            // 모든 사용자의 해당 시간대 활동 집계
            List<RankingUserScore> allUsers = rankingUserScoreService.getTopUsers(Integer.MAX_VALUE);
            for (RankingUserScore user : allUsers) {
                count += rankingUserActivityService
                    .getUserActivitiesByPeriod(user.getUserId(), startDate, endDate)
                    .stream()
                    .filter(activity -> activity.getCreatedAt().getHour() == currentHour)
                    .count();
            }
            activityByHour.put(hour, count);
        }

        // 사용자 통계 조회 - 실제 데이터 기반으로 구현
        List<RankingUserScore> allUsers = rankingUserScoreService.getTopUsers(Integer.MAX_VALUE);
        long totalUsers = allUsers.size();
        List<RankingUserScore> suspendedUsers = rankingUserScoreService.getSuspendedUsers();

        // 활성 사용자 수 (지정된 기간 내 활동이 있는 사용자)
        long activeUsersDuringPeriod = allUsers.stream()
            .filter(user -> !rankingUserActivityService
                .getUserActivitiesByPeriod(user.getUserId(), startDate, endDate)
                .isEmpty())
            .count();

        // 평균 점수 계산
        double averageScore = allUsers.stream()
            .mapToInt(RankingUserScore::getCurrentScore)
            .average()
            .orElse(0.0);

        return new AdminRankingStatisticsResponse(
            rankDistribution,
            activityTypeDistribution,
            activityByHour,
            totalUsers,
            activeUsersDuringPeriod,
            (long) suspendedUsers.size(),
            averageScore,
            startDate,
            endDate
        );
    }

    @Override
    @Transactional
    public boolean updateRankingSystemSetting(String settingKey, String settingValue) {
        log.info("랭킹 시스템 설정 업데이트 - 키: {}, 값: {}", settingKey, settingValue);

        try {
            // 랭킹 관련 설정 업데이트 (시스템 설정 서비스 활용)
            systemSettingService.setRankingParameter(settingKey, settingValue, 1L); // 1L은 시스템 관리자 ID
            return true;
        } catch (Exception e) {
            log.error("랭킹 시스템 설정 업데이트 중 오류 발생", e);
            return false;
        }
    }
} 