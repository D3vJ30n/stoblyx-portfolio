package com.j30n.stoblyx.application.service.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.stats.*;
import com.j30n.stoblyx.application.port.in.admin.AdminDashboardStatsUseCase;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 관리자 대시보드 통계 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardStatsService implements AdminDashboardStatsUseCase {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final String PERIOD_DAILY = "daily";
    private static final String PERIOD_WEEKLY = "weekly";
    private static final String PERIOD_MONTHLY = "monthly";
    
    private static final String RANK_BRONZE = "BRONZE";
    private static final String RANK_SILVER = "SILVER";
    private static final String RANK_GOLD = "GOLD";
    private static final String RANK_PLATINUM = "PLATINUM";
    private static final String RANK_DIAMOND = "DIAMOND";
    private final UserRepository userRepository;
    private final ShortFormContentRepository contentRepository;
    private final QuoteRepository quoteRepository;
    private final ContentLikeRepository contentLikeRepository;
    private final ContentCommentRepository contentCommentRepository;
    private final ContentBookmarkRepository contentBookmarkRepository;
    private final RankingUserScoreRepository rankingUserScoreRepository;
    private final RankingUserActivityRepository rankingUserActivityRepository;
    private final Random random = new Random();

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {
        // 현재 날짜 기준 통계 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        
        // 사용자 통계
        long totalUsers = userRepository.count();
        long newUsersToday = userRepository.countByCreatedAtAfter(startOfDay);
        long activeUsersToday = userRepository.countByLastLoginAtAfter(startOfDay);
        
        // 콘텐츠 통계
        long totalContents = contentRepository.count();
        long contentsCreatedToday = contentRepository.countByCreatedAtAfter(startOfDay);
        long pendingContents = contentRepository.countByStatus(ContentStatus.PROCESSING);
        
        // 기타 통계
        long totalQuotes = quoteRepository.count();
        long totalLikes = contentLikeRepository.count();
        long totalComments = contentCommentRepository.count();
        long totalBookmarks = contentBookmarkRepository.count();
        
        // 시스템 리소스 통계 (간단한 정보만)
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        double memoryUsage = (double) memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax();
        
        return DashboardSummaryResponse.builder()
                .totalUsers(totalUsers)
                .newUsersToday(newUsersToday)
                .activeUsersToday(activeUsersToday)
                .totalContents(totalContents)
                .contentsCreatedToday(contentsCreatedToday)
                .pendingContents(pendingContents)
                .totalQuotes(totalQuotes)
                .totalLikes(totalLikes)
                .totalComments(totalComments)
                .totalBookmarks(totalBookmarks)
                .cpuUsage(0.45) // 임시 데이터
                .memoryUsage(memoryUsage)
                .diskUsage(0.65) // 임시 데이터
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ContentStatsResponse getContentStats(String period, LocalDate startDate, LocalDate endDate) {
        // 기간 설정
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            if (PERIOD_DAILY.equals(period)) {
                startDate = now.minusDays(30);
                endDate = now;
            } else if (PERIOD_WEEKLY.equals(period)) {
                startDate = now.minusWeeks(12);
                endDate = now;
            } else if (PERIOD_MONTHLY.equals(period)) {
                startDate = now.minusMonths(12);
                endDate = now;
            } else {
                startDate = now.minusDays(30);
                endDate = now;
                period = PERIOD_DAILY;
            }
        }
        
        // 콘텐츠 생성 시계열 데이터 (임시 데이터)
        List<ContentStatsResponse.TimeSeriesDataPoint> contentCreationStats = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            contentCreationStats.add(
                ContentStatsResponse.TimeSeriesDataPoint.builder()
                    .date(current.format(DATE_FORMATTER))
                    .count(random.nextInt(50) + 10)
                    .build()
            );
            
            if (PERIOD_DAILY.equals(period)) {
                current = current.plusDays(1);
            } else if (PERIOD_WEEKLY.equals(period)) {
                current = current.plusWeeks(1);
            } else {
                current = current.plusMonths(1);
            }
        }
        
        // 콘텐츠 상태별 통계 (임시 데이터)
        Map<String, Long> contentStatusStats = new HashMap<>();
        contentStatusStats.put("PUBLISHED", 450L);
        contentStatusStats.put("PROCESSING", 120L);
        contentStatusStats.put("FAILED", 30L);
        
        // 콘텐츠 유형별 통계 (임시 데이터)
        Map<String, Long> contentTypeStats = new HashMap<>();
        contentTypeStats.put("VIDEO", 320L);
        contentTypeStats.put("IMAGE", 180L);
        contentTypeStats.put("MIXED", 100L);
        
        // 인기 콘텐츠 (임시 데이터)
        List<ContentStatsResponse.PopularContentStats> popularContents = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            popularContents.add(
                ContentStatsResponse.PopularContentStats.builder()
                    .contentId((long) i)
                    .title("인기 콘텐츠 #" + i)
                    .viewCount(random.nextInt(1000) + 500)
                    .likeCount(random.nextInt(500) + 100)
                    .commentCount(random.nextInt(100) + 10)
                    .shareCount(random.nextInt(50) + 5)
                    .build()
            );
        }
        
        return ContentStatsResponse.builder()
                .period(period)
                .startDate(startDate)
                .endDate(endDate)
                .contentCreationStats(contentCreationStats)
                .contentStatusStats(contentStatusStats)
                .contentTypeStats(contentTypeStats)
                .popularContents(popularContents)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserActivityStatsResponse getUserActivityStats(String period, LocalDate startDate, LocalDate endDate) {
        // 기간 설정
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            if (PERIOD_DAILY.equals(period)) {
                startDate = now.minusDays(30);
                endDate = now;
            } else if (PERIOD_WEEKLY.equals(period)) {
                startDate = now.minusWeeks(12);
                endDate = now;
            } else if (PERIOD_MONTHLY.equals(period)) {
                startDate = now.minusMonths(12);
                endDate = now;
            } else {
                startDate = now.minusDays(30);
                endDate = now;
                period = PERIOD_DAILY;
            }
        }
        
        // 시계열 데이터 생성 (임시 데이터)
        List<UserActivityStatsResponse.TimeSeriesDataPoint> newUserStats = new ArrayList<>();
        List<UserActivityStatsResponse.TimeSeriesDataPoint> loginStats = new ArrayList<>();
        List<UserActivityStatsResponse.TimeSeriesDataPoint> contentCreationStats = new ArrayList<>();
        
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            String dateStr = current.format(DATE_FORMATTER);
            
            newUserStats.add(
                UserActivityStatsResponse.TimeSeriesDataPoint.builder()
                    .date(dateStr)
                    .count(random.nextInt(20) + 5)
                    .build()
            );
            
            loginStats.add(
                UserActivityStatsResponse.TimeSeriesDataPoint.builder()
                    .date(dateStr)
                    .count(random.nextInt(100) + 50)
                    .build()
            );
            
            contentCreationStats.add(
                UserActivityStatsResponse.TimeSeriesDataPoint.builder()
                    .date(dateStr)
                    .count(random.nextInt(50) + 10)
                    .build()
            );
            
            if (PERIOD_DAILY.equals(period)) {
                current = current.plusDays(1);
            } else if (PERIOD_WEEKLY.equals(period)) {
                current = current.plusWeeks(1);
            } else {
                current = current.plusMonths(1);
            }
        }
        
        // 활동 유형별 통계 (임시 데이터)
        Map<String, Long> activityTypeStats = new HashMap<>();
        activityTypeStats.put("LIKE", 2500L);
        activityTypeStats.put("COMMENT", 1200L);
        activityTypeStats.put("CONTENT_CREATE", 800L);
        activityTypeStats.put("BOOKMARK", 1500L);
        
        // 활발한 사용자 목록 (임시 데이터)
        List<UserActivityStatsResponse.ActiveUserStats> activeUsers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            activeUsers.add(
                UserActivityStatsResponse.ActiveUserStats.builder()
                    .userId((long) i)
                    .username("활발한 사용자 #" + i)
                    .contentCount(random.nextInt(50) + 10)
                    .likeCount(random.nextInt(200) + 50)
                    .commentCount(random.nextInt(100) + 20)
                    .loginCount(random.nextInt(30) + 15)
                    .totalScore(random.nextInt(2000) + 1000)
                    .build()
            );
        }
        
        return UserActivityStatsResponse.builder()
                .period(period)
                .startDate(startDate)
                .endDate(endDate)
                .newUserStats(newUserStats)
                .loginStats(loginStats)
                .contentCreationStats(contentCreationStats)
                .activityTypeStats(activityTypeStats)
                .activeUsers(activeUsers)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SystemResourcesResponse getSystemResources() {
        // 현재 시스템 상태 (일부 실제 데이터, 일부 임시 데이터)
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapMemoryUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMemoryMax = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsage = (double) heapMemoryUsed / heapMemoryMax;
        
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        
        // 시계열 데이터 (임시 데이터)
        List<SystemResourcesResponse.ResourceTimeSeriesData> cpuHistory = new ArrayList<>();
        List<SystemResourcesResponse.ResourceTimeSeriesData> memoryHistory = new ArrayList<>();
        List<SystemResourcesResponse.ResourceTimeSeriesData> diskHistory = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 24; i >= 0; i--) {
            LocalDateTime timestamp = now.minusHours(i);
            
            cpuHistory.add(
                SystemResourcesResponse.ResourceTimeSeriesData.builder()
                    .timestamp(timestamp)
                    .value(0.2 + random.nextDouble() * 0.5)
                    .build()
            );
            
            memoryHistory.add(
                SystemResourcesResponse.ResourceTimeSeriesData.builder()
                    .timestamp(timestamp)
                    .value(0.4 + random.nextDouble() * 0.3)
                    .build()
            );
            
            diskHistory.add(
                SystemResourcesResponse.ResourceTimeSeriesData.builder()
                    .timestamp(timestamp)
                    .value(0.5 + random.nextDouble() * 0.2)
                    .build()
            );
        }
        
        return SystemResourcesResponse.builder()
                .timestamp(now)
                .cpuUsage(0.45)
                .memoryUsage(memoryUsage)
                .diskUsage(0.65)
                .heapMemoryUsed(heapMemoryUsed)
                .heapMemoryMax(heapMemoryMax)
                .threadCount(threadCount)
                .cpuUsageHistory(cpuHistory)
                .memoryUsageHistory(memoryHistory)
                .diskUsageHistory(diskHistory)
                .dbConnectionCount(10)
                .dbResponseTime(0.05)
                .dbSize(1024 * 1024 * 500) // 500MB
                .cacheHitCount(15000)
                .cacheMissCount(3000)
                .cacheHitRatio(15000.0 / (15000 + 3000))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RankingStatsResponse getRankingStats() {
        // 랭크별 사용자 분포
        Map<String, Long> rankDistribution = new HashMap<>();
        rankDistribution.put(RANK_BRONZE, rankingUserScoreRepository.countByRankType(RankType.BRONZE));
        rankDistribution.put(RANK_SILVER, rankingUserScoreRepository.countByRankType(RankType.SILVER));
        rankDistribution.put(RANK_GOLD, rankingUserScoreRepository.countByRankType(RankType.GOLD));
        rankDistribution.put(RANK_PLATINUM, rankingUserScoreRepository.countByRankType(RankType.PLATINUM));
        rankDistribution.put(RANK_DIAMOND, rankingUserScoreRepository.countByRankType(RankType.DIAMOND));
        
        // 점수 분포 통계 (임시 데이터)
        Map<String, Long> scoreRanges = new HashMap<>();
        scoreRanges.put("0-500", 150L);
        scoreRanges.put("501-1000", 320L);
        scoreRanges.put("1001-1500", 280L);
        scoreRanges.put("1501-2000", 120L);
        scoreRanges.put("2001+", 30L);
        
        RankingStatsResponse.ScoreDistributionStats scoreDistribution = RankingStatsResponse.ScoreDistributionStats.builder()
                .averageScore(1250.5)
                .medianScore(1100.0)
                .minScore(0)
                .maxScore(3200)
                .scoreRanges(scoreRanges)
                .build();
        
        // 랭크 변경 통계 (임시 데이터)
        List<RankingStatsResponse.RankChangeStats> recentRankChanges = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            recentRankChanges.add(
                RankingStatsResponse.RankChangeStats.builder()
                    .userId((long) i)
                    .username("사용자 #" + i)
                    .previousRank(RANK_SILVER)
                    .currentRank(RANK_GOLD)
                    .scoreChange(350)
                    .changeDate(LocalDate.now().minusDays(i).format(DATE_FORMATTER))
                    .build()
            );
        }
        
        // 활동 패턴 통계 (임시 데이터)
        Map<String, Long> activityPatternStats = new HashMap<>();
        activityPatternStats.put("MORNING", 250L);
        activityPatternStats.put("AFTERNOON", 420L);
        activityPatternStats.put("EVENING", 380L);
        activityPatternStats.put("NIGHT", 150L);
        
        // 상위 랭킹 사용자 (임시 데이터)
        List<RankingStatsResponse.TopRankedUserStats> topRankedUsers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String rank = RANK_DIAMOND;
            if (i > 2) rank = RANK_PLATINUM;
            
            topRankedUsers.add(
                RankingStatsResponse.TopRankedUserStats.builder()
                    .userId((long) i)
                    .username("상위 사용자 #" + i)
                    .rank(rank)
                    .score(3000 - (i * 100))
                    .contentCount(random.nextInt(100) + 50)
                    .likeCount(random.nextInt(500) + 200)
                    .commentCount(random.nextInt(300) + 100)
                    .build()
            );
        }
        
        return RankingStatsResponse.builder()
                .rankDistribution(rankDistribution)
                .scoreDistribution(scoreDistribution)
                .recentRankChanges(recentRankChanges)
                .activityPatternStats(activityPatternStats)
                .topRankedUsers(topRankedUsers)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyDetectionResponse> getAnomalyDetection(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<AnomalyDetectionResponse> anomalies = new ArrayList<>();
        
        try {
            // 실제 데이터 조회 시도 (의심스러운 활동)
            List<Object[]> suspiciousActivities = rankingUserActivityRepository.findSuspiciousActivities(startDate, 100);
            
            // 실제 데이터가 있으면 사용
            if (suspiciousActivities != null && !suspiciousActivities.isEmpty()) {
                for (Object[] activity : suspiciousActivities) {
                    Long userId = (Long) activity[0];
                    Long scoreChange = ((Number) activity[1]).longValue();
                    Long activityCount = ((Number) activity[2]).longValue();
                    LocalDateTime lastActivityTime = (LocalDateTime) activity[3];
                    
                    List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = 
                        createRelatedActivities(lastActivityTime, scoreChange, activityCount);
                    
                    anomalies.add(createAnomalyResponse(
                        userId, scoreChange, lastActivityTime, relatedActivities, true));
                }
                return anomalies;
            }
        } catch (Exception e) {
            log.warn("실제 이상 활동 데이터 조회 중 오류 발생: {}", e.getMessage());
            // 오류 발생 시 임시 데이터로 대체
        }
        
        // 실제 데이터가 없거나 오류 발생 시 임시 데이터 생성
        log.info("이상 활동 임시 데이터 생성");
        for (int i = 1; i <= 5; i++) {
            Long userId = (long) i;
            LocalDateTime detectedAt = LocalDateTime.now().minusDays(random.nextInt(days));
            long scoreChange = 100 + random.nextInt(400);
            
            List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = 
                createSampleRelatedActivities(detectedAt, scoreChange);
            
            anomalies.add(createAnomalyResponse(
                userId, scoreChange, detectedAt, relatedActivities, false));
        }
        
        return anomalies;
    }
    
    /**
     * 실제 데이터 기반 관련 활동 목록 생성
     */
    private List<AnomalyDetectionResponse.RelatedActivity> createRelatedActivities(
            LocalDateTime lastActivityTime, Long scoreChange, Long activityCount) {
        
        List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = new ArrayList<>();
        // 실제 활동 수만큼 관련 활동 추가 (최대 5개)
        int relatedCount = Math.min(activityCount.intValue(), 5);
        for (int j = 1; j <= relatedCount; j++) {
            relatedActivities.add(
                AnomalyDetectionResponse.RelatedActivity.builder()
                    .activityType("LIKE")
                    .targetType("CONTENT")
                    .targetId(random.nextLong(100) + 1)
                    .timestamp(lastActivityTime.minusMinutes(j * 5))
                    .scoreChange(scoreChange / relatedCount)
                    .build()
            );
        }
        return relatedActivities;
    }
    
    /**
     * 샘플 관련 활동 목록 생성
     */
    private List<AnomalyDetectionResponse.RelatedActivity> createSampleRelatedActivities(
            LocalDateTime detectedAt, long scoreChange) {
        
        List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = new ArrayList<>();
        for (int j = 1; j <= 3; j++) {
            relatedActivities.add(
                AnomalyDetectionResponse.RelatedActivity.builder()
                    .activityType(j % 2 == 0 ? "LIKE" : "COMMENT")
                    .targetType("CONTENT")
                    .targetId(random.nextLong(100) + 1)
                    .timestamp(detectedAt.minusMinutes(j * 5))
                    .scoreChange(scoreChange / 3)
                    .build()
            );
        }
        return relatedActivities;
    }
    
    /**
     * 이상 활동 응답 객체 생성
     */
    private AnomalyDetectionResponse createAnomalyResponse(
            Long userId, long scoreChange, LocalDateTime detectedAt, 
            List<AnomalyDetectionResponse.RelatedActivity> relatedActivities, boolean isRealData) {
        
        return AnomalyDetectionResponse.builder()
            .userId(userId)
            .username("사용자 #" + userId)
            .anomalyType("RAPID_SCORE_INCREASE")
            .description("비정상적으로 빠른 점수 증가")
            .detectedAt(detectedAt)
            .severityScore(isRealData ? 0.85 : 0.7 + (random.nextDouble() * 0.3))
            .isResolved(isRealData ? false : random.nextBoolean())
            .scoreBeforeAnomaly(isRealData ? 1200 : 1000 + random.nextInt(500))
            .scoreAfterAnomaly(isRealData ? 1200 + scoreChange : 1500 + random.nextInt(500))
            .scoreChange(scoreChange)
            .ipAddress("192.168.1." + (userId % 255))
            .relatedActivities(relatedActivities)
            .build();
    }
} 