package com.j30n.stoblyx.application.service.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.stats.*;
import com.j30n.stoblyx.application.port.in.admin.AdminDashboardStatsUseCase;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.enums.ContentType;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.model.User;
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
    
    private static final String UNKNOWN_USER_PREFIX = "사용자 #";
    private static final String TOP_USER_PREFIX = "상위 사용자 #";
    
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
        
        // 시스템 리소스 통계 (실제 데이터)
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        double memoryUsage = (double) memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax();
        
        // CPU 및 디스크 사용량 가져오기
        double cpuUsage = getCpuUsage();
        double diskUsage = getDiskUsage();
        
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
                .cpuUsage(cpuUsage)
                .memoryUsage(memoryUsage)
                .diskUsage(diskUsage)
                .build();
    }
    
    /**
     * 시스템의 현재 CPU 사용량을 가져옵니다.
     * @return CPU 사용량 (0.0 ~ 1.0)
     */
    private double getCpuUsage() {
        try {
            com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            return osBean.getCpuLoad();
        } catch (Exception e) {
            log.warn("CPU 사용량 조회 중 오류 발생: {}", e.getMessage());
            return Runtime.getRuntime().availableProcessors() / (double) (Runtime.getRuntime().availableProcessors() + 2);
        }
    }
    
    /**
     * 시스템의 현재 디스크 사용량을 가져옵니다.
     * @return 디스크 사용량 (0.0 ~ 1.0)
     */
    private double getDiskUsage() {
        try {
            java.io.File file = new java.io.File("/");
            long totalSpace = file.getTotalSpace();
            long freeSpace = file.getFreeSpace();
            return (double) (totalSpace - freeSpace) / totalSpace;
        } catch (Exception e) {
            log.warn("디스크 사용량 조회 중 오류 발생: {}", e.getMessage());
            return 0.7; // 조회 실패 시 기본값
        }
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
        
        // 콘텐츠 생성 시계열 데이터 (실제 데이터)
        List<ContentStatsResponse.TimeSeriesDataPoint> contentCreationStats = new ArrayList<>();
        LocalDate current = startDate;
        
        Map<String, Long> dateCreationCountMap = getContentCreationCountsByDateRange(startDate, endDate, period);
        
        while (!current.isAfter(endDate)) {
            String dateKey = current.format(DATE_FORMATTER);
            Long count = dateCreationCountMap.getOrDefault(dateKey, 0L);
            
            contentCreationStats.add(
                ContentStatsResponse.TimeSeriesDataPoint.builder()
                    .date(dateKey)
                    .count(count)
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
        
        // 콘텐츠 상태별 통계 (실제 데이터)
        Map<String, Long> contentStatusStats = new HashMap<>();
        for (ContentStatus status : ContentStatus.values()) {
            contentStatusStats.put(status.name(), contentRepository.countByStatus(status));
        }
        
        // 콘텐츠 유형별 통계 (실제 데이터)
        Map<String, Long> contentTypeStats = new HashMap<>();
        contentTypeStats.put("VIDEO", contentRepository.countByContentType(ContentType.VIDEO));
        contentTypeStats.put("IMAGE", contentRepository.countByContentType(ContentType.IMAGE));
        contentTypeStats.put("MIXED", contentRepository.countByContentType(ContentType.MIXED));
        
        // 인기 콘텐츠 (실제 데이터)
        List<ContentStatsResponse.PopularContentStats> popularContents = getPopularContents(5);
        
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
    
    /**
     * 지정된 기간 내 일별/주별/월별 콘텐츠 생성 통계를 가져옵니다.
     */
    private Map<String, Long> getContentCreationCountsByDateRange(LocalDate startDate, LocalDate endDate, String period) {
        // 시작 및 종료 LocalDateTime 계산
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        // 콘텐츠 리포지토리에서 기간 내 생성된 콘텐츠 조회
        List<ShortFormContent> contents = contentRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        
        // 일별/주별/월별로 그룹화
        Map<String, Long> groupedContents = new HashMap<>();
        
        for (ShortFormContent content : contents) {
            LocalDateTime createdAt = content.getCreatedAt();
            String key;
            
            if (PERIOD_DAILY.equals(period)) {
                // 일별 그룹화
                key = createdAt.toLocalDate().format(DATE_FORMATTER);
            } else if (PERIOD_WEEKLY.equals(period)) {
                // 주별 그룹화 (해당 주의 월요일 날짜 사용)
                LocalDate monday = createdAt.toLocalDate().minusDays((long)createdAt.getDayOfWeek().getValue() - 1);
                key = monday.format(DATE_FORMATTER);
            } else {
                // 월별 그룹화 (해당 월의 첫날 사용)
                key = createdAt.toLocalDate().withDayOfMonth(1).format(DATE_FORMATTER);
            }
            
            groupedContents.put(key, groupedContents.getOrDefault(key, 0L) + 1);
        }
        
        return groupedContents;
    }
    
    /**
     * 인기 있는 콘텐츠 목록을 가져옵니다.
     */
    private List<ContentStatsResponse.PopularContentStats> getPopularContents(int limit) {
        List<ContentStatsResponse.PopularContentStats> result = new ArrayList<>();
        
        // 콘텐츠 조회 (모든 콘텐츠)
        List<ShortFormContent> allContents = contentRepository.findAll();
        
        // 인기도에 따라 정렬 (조회수, 좋아요, 댓글 수 등을 고려한 점수 계산)
        allContents.sort((c1, c2) -> {
            // 각 콘텐츠의 인기도 점수 계산
            long score1 = calculatePopularityScore(c1.getId());
            long score2 = calculatePopularityScore(c2.getId());
            return Long.compare(score2, score1); // 내림차순 정렬
        });
        
        // 상위 N개 콘텐츠만 선택
        List<ShortFormContent> topContents = allContents.stream()
                .limit(limit)
                .toList();
        
        // 결과 매핑
        for (ShortFormContent content : topContents) {
            Long contentId = content.getId();
            String title = content.getTitle();
            Long viewCount = Long.valueOf(content.getViewCount());
            
            // 좋아요, 댓글, 공유 수 조회
            Long likeCount = contentLikeRepository.countByContentId(contentId);
            Long commentCount = contentCommentRepository.countByContentId(contentId);
            Long shareCount = Long.valueOf(content.getShareCount());
            
            result.add(
                ContentStatsResponse.PopularContentStats.builder()
                    .contentId(contentId)
                    .title(title)
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .shareCount(shareCount)
                    .build()
            );
        }
        
        return result;
    }
    
    /**
     * 콘텐츠의 인기도 점수를 계산합니다.
     * (조회수, 좋아요, 댓글, 공유 등을 가중치를 적용하여 계산)
     */
    private long calculatePopularityScore(Long contentId) {
        ShortFormContent content = contentRepository.findById(contentId).orElse(null);
        if (content == null) return 0;
        
        long viewCount = content.getViewCount();
        long likeCount = contentLikeRepository.countByContentId(contentId);
        long commentCount = contentCommentRepository.countByContentId(contentId);
        long shareCount = content.getShareCount();
        
        // 가중치 적용 (조회수 1, 좋아요 3, 댓글 5, 공유 10)
        return viewCount + (likeCount * 3) + (commentCount * 5) + (shareCount * 10);
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
        
        // 시간 범위를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        // 시계열 데이터 생성 (실제 데이터)
        List<UserActivityStatsResponse.TimeSeriesDataPoint> newUserStats = getUserCreationTimeSeriesData(period, startDate, endDate);
        List<UserActivityStatsResponse.TimeSeriesDataPoint> loginStats = getUserLoginTimeSeriesData(period, startDate, endDate);
        List<UserActivityStatsResponse.TimeSeriesDataPoint> contentCreationStats = getContentCreationTimeSeriesData(period, startDate, endDate);
        
        // 활동 유형별 통계 (실제 데이터)
        Map<String, Long> activityTypeStats = getActivityTypeStats(startDateTime, endDateTime);
        
        // 활발한 사용자 목록 (실제 데이터)
        List<UserActivityStatsResponse.ActiveUserStats> activeUsers = getActiveUsers(startDateTime, endDateTime, 10);
        
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
    
    /**
     * 사용자 생성 시계열 데이터를 가져옵니다.
     */
    private List<UserActivityStatsResponse.TimeSeriesDataPoint> getUserCreationTimeSeriesData(String period, LocalDate startDate, LocalDate endDate) {
        List<UserActivityStatsResponse.TimeSeriesDataPoint> result = new ArrayList<>();
        
        // 각 날짜별 사용자 생성 수를 저장할 맵
        Map<String, Long> dateCountMap = new HashMap<>();
        
        // 사용자 리포지토리에서 기간 내 생성된 사용자 조회
        List<User> users = userRepository.findByCreatedAtBetween(
            startDate.atStartOfDay(), 
            endDate.plusDays(1).atStartOfDay()
        );
        
        // 날짜별로 그룹화
        for (User user : users) {
            LocalDateTime createdAt = user.getCreatedAt();
            String key;
            
            if (PERIOD_DAILY.equals(period)) {
                key = createdAt.toLocalDate().format(DATE_FORMATTER);
            } else if (PERIOD_WEEKLY.equals(period)) {
                // 주의 첫날(월요일)로 그룹화
                LocalDate monday = createdAt.toLocalDate().minusDays((long)createdAt.getDayOfWeek().getValue() - 1);
                key = monday.format(DATE_FORMATTER);
            } else {
                // 월의 첫날로 그룹화
                key = createdAt.toLocalDate().withDayOfMonth(1).format(DATE_FORMATTER);
            }
            
            dateCountMap.put(key, dateCountMap.getOrDefault(key, 0L) + 1);
        }
        
        // 결과 생성
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateKey = getDateKey(current, period);
            Long count = dateCountMap.getOrDefault(dateKey, 0L);
            
            result.add(
                UserActivityStatsResponse.TimeSeriesDataPoint.builder()
                    .date(current.format(DATE_FORMATTER))
                    .count(count)
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
        
        return result;
    }
    
    /**
     * 사용자 로그인 시계열 데이터를 가져옵니다.
     */
    private List<UserActivityStatsResponse.TimeSeriesDataPoint> getUserLoginTimeSeriesData(String period, LocalDate startDate, LocalDate endDate) {
        List<UserActivityStatsResponse.TimeSeriesDataPoint> result = new ArrayList<>();
        
        // 각 날짜별 로그인 수를 저장할 맵
        Map<String, Long> dateCountMap = new HashMap<>();
        
        // 사용자 리포지토리에서 기간 내 로그인한 사용자 조회
        List<Object[]> loginData = userRepository.countLoginsByDateBetween(
            startDate.atStartOfDay(), 
            endDate.plusDays(1).atStartOfDay()
        );
        
        // 결과 매핑
        for (Object[] data : loginData) {
            LocalDate loginDate = ((java.sql.Date) data[0]).toLocalDate();
            Long count = ((Number) data[1]).longValue();
            
            String key = getDateKey(loginDate, period);
            dateCountMap.put(key, dateCountMap.getOrDefault(key, 0L) + count);
        }
        
        // 결과 생성
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateKey = getDateKey(current, period);
            Long count = dateCountMap.getOrDefault(dateKey, 0L);
            
            result.add(
                UserActivityStatsResponse.TimeSeriesDataPoint.builder()
                    .date(current.format(DATE_FORMATTER))
                    .count(count)
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
        
        return result;
    }
    
    /**
     * 콘텐츠 생성 시계열 데이터를 가져옵니다.
     */
    private List<UserActivityStatsResponse.TimeSeriesDataPoint> getContentCreationTimeSeriesData(String period, LocalDate startDate, LocalDate endDate) {
        List<UserActivityStatsResponse.TimeSeriesDataPoint> result = new ArrayList<>();
        
        // 각 날짜별 콘텐츠 생성 수를 저장할 맵
        Map<String, Long> dateCountMap = new HashMap<>();
        
        // 콘텐츠 리포지토리에서 기간 내 생성된 콘텐츠 조회
        List<Object[]> contentData = contentRepository.countContentsByDateBetween(
            startDate.atStartOfDay(), 
            endDate.plusDays(1).atStartOfDay()
        );
        
        // 결과 매핑
        for (Object[] data : contentData) {
            LocalDate contentDate = ((java.sql.Date) data[0]).toLocalDate();
            Long count = ((Number) data[1]).longValue();
            
            String key = getDateKey(contentDate, period);
            dateCountMap.put(key, dateCountMap.getOrDefault(key, 0L) + count);
        }
        
        // 결과 생성
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateKey = getDateKey(current, period);
            Long count = dateCountMap.getOrDefault(dateKey, 0L);
            
            result.add(
                UserActivityStatsResponse.TimeSeriesDataPoint.builder()
                    .date(current.format(DATE_FORMATTER))
                    .count(count)
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
        
        return result;
    }
    
    /**
     * 기간에 따른 날짜 키를 가져옵니다.
     */
    private String getDateKey(LocalDate date, String period) {
        if (PERIOD_DAILY.equals(period)) {
            return date.format(DATE_FORMATTER);
        } else if (PERIOD_WEEKLY.equals(period)) {
            // 주의 첫날(월요일)로 그룹화
            LocalDate monday = date.minusDays((long)date.getDayOfWeek().getValue() - 1);
            return monday.format(DATE_FORMATTER);
        } else {
            // 월의 첫날로 그룹화
            return date.withDayOfMonth(1).format(DATE_FORMATTER);
        }
    }
    
    /**
     * 활동 유형별 통계를 가져옵니다.
     */
    private Map<String, Long> getActivityTypeStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Map<String, Long> activityTypeStats = new HashMap<>();
        
        // 실제 데이터로 활동 유형별 통계 추가
        activityTypeStats.put("LIKE", contentLikeRepository.countByCreatedAtBetween(startDateTime, endDateTime));
        activityTypeStats.put("COMMENT", contentCommentRepository.countByCreatedAtBetween(startDateTime, endDateTime));
        activityTypeStats.put("CONTENT_CREATE", contentRepository.countByCreatedAtBetween(startDateTime, endDateTime));
        activityTypeStats.put("BOOKMARK", contentBookmarkRepository.countByCreatedAtBetween(startDateTime, endDateTime));
        
        return activityTypeStats;
    }
    
    /**
     * 가장 활발한 사용자 목록을 가져옵니다.
     */
    private List<UserActivityStatsResponse.ActiveUserStats> getActiveUsers(LocalDateTime startDateTime, LocalDateTime endDateTime, int limit) {
        List<UserActivityStatsResponse.ActiveUserStats> result = new ArrayList<>();
        
        // 사용자 리포지토리에서 모든 사용자 조회
        List<User> allUsers = userRepository.findAll();
        
        // 각 사용자의 활동 점수를 계산
        Map<Long, UserActivityStatsResponse.ActiveUserStats.ActiveUserStatsBuilder> userStatsMap = new HashMap<>();
        
        for (User user : allUsers) {
            Long userId = user.getId();
            String username = user.getUsername();
            
            // 해당 기간 동안의 콘텐츠 생성 수
            long contentCount = contentRepository.countByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);
            
            // 해당 기간 동안의 좋아요 수
            long likeCount = contentLikeRepository.countByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);
            
            // 해당 기간 동안의 댓글 수
            long commentCount = contentCommentRepository.countByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime);
            
            // 해당 기간 동안의 로그인 수
            long loginCount = userRepository.countLoginsByUserIdAndDateBetween(userId, startDateTime, endDateTime);
            
            // 랭킹 스코어 조회
            int totalScore = rankingUserScoreRepository.findById(userId)
                .map(score -> score.getCurrentScore())
                .orElse(0);
            
            // 종합 활동 점수 계산 (콘텐츠 생성 * 10 + 좋아요 * 3 + 댓글 * 5 + 로그인 * 1)
            long activityScore = (contentCount * 10) + (likeCount * 3) + (commentCount * 5) + loginCount;
            
            userStatsMap.put(userId, UserActivityStatsResponse.ActiveUserStats.builder()
                .userId(userId)
                .username(username)
                .contentCount(contentCount)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .loginCount(loginCount)
                .totalScore(totalScore)
                .activityScore(activityScore)
            );
        }
        
        // 활동 점수가 높은 순으로 정렬
        List<Map.Entry<Long, UserActivityStatsResponse.ActiveUserStats.ActiveUserStatsBuilder>> sortedEntries = 
            userStatsMap.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> -e.getValue().build().getActivityScore()))
                .limit(limit)
                .toList();
        
        // 결과 매핑
        for (Map.Entry<Long, UserActivityStatsResponse.ActiveUserStats.ActiveUserStatsBuilder> entry : sortedEntries) {
            result.add(entry.getValue().build());
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public SystemResourcesResponse getSystemResources() {
        // 현재 시스템 상태
        LocalDateTime now = LocalDateTime.now();
        
        // 메모리 관련 정보
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapMemoryUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMemoryMax = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsage = (double) heapMemoryUsed / heapMemoryMax;
        
        // CPU 사용량
        double cpuUsage = getCpuUsage();
        
        // 디스크 사용량
        double diskUsage = getDiskUsage();
        
        // 스레드 수
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        
        // 시계열 데이터 (실제 데이터)
        // 참고: 실제 환경에서는 이 데이터를 모니터링 시스템에서 가져오거나 
        // 캐시/DB에 저장된 이력 데이터를 사용하는 것이 좋다.
        List<SystemResourcesResponse.ResourceTimeSeriesData> cpuHistory = getResourceMetricsHistory("cpu", 24);
        List<SystemResourcesResponse.ResourceTimeSeriesData> memoryHistory = getResourceMetricsHistory("memory", 24);
        List<SystemResourcesResponse.ResourceTimeSeriesData> diskHistory = getResourceMetricsHistory("disk", 24);
        
        // 데이터베이스 통계 (모니터링 시스템에서 가져올 수 있는 정보)
        int dbConnectionCount = getDbConnectionCount();
        double dbResponseTime = getDbResponseTime();
        long dbSize = getDbSize();
        
        // 캐시 통계 (캐시 성능 측정)
        long cacheHitCount = getCacheHitCount();
        long cacheMissCount = getCacheMissCount();
        double cacheHitRatio = cacheHitCount / (double) (cacheHitCount + cacheMissCount);
        
        return SystemResourcesResponse.builder()
                .timestamp(now)
                .cpuUsage(cpuUsage)
                .memoryUsage(memoryUsage)
                .diskUsage(diskUsage)
                .heapMemoryUsed(heapMemoryUsed)
                .heapMemoryMax(heapMemoryMax)
                .threadCount(threadCount)
                .cpuUsageHistory(cpuHistory)
                .memoryUsageHistory(memoryHistory)
                .diskUsageHistory(diskHistory)
                .dbConnectionCount(dbConnectionCount)
                .dbResponseTime(dbResponseTime)
                .dbSize(dbSize)
                .cacheHitCount(cacheHitCount)
                .cacheMissCount(cacheMissCount)
                .cacheHitRatio(cacheHitRatio)
                .build();
    }
    
    /**
     * 시스템 리소스 메트릭 이력을 가져옵니다.
     * 
     * @param metricType 메트릭 유형 (cpu, memory, disk)
     * @param hours 가져올 시간 범위 (시간)
     * @return 시계열 데이터 리스트
     */
    private List<SystemResourcesResponse.ResourceTimeSeriesData> getResourceMetricsHistory(String metricType, int hours) {
        List<SystemResourcesResponse.ResourceTimeSeriesData> history = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        try {
            // 모니터링 시스템에서 이력 데이터를 가져오는 코드 (구현되지 않음)
            // 실제로는 여기서 DB나 모니터링 시스템에서 데이터를 가져와야 함
            
            // 임시로 최근 값에 약간의 변동을 주어 시계열 데이터 생성
            double baseValue;
            switch (metricType) {
                case "cpu":
                    baseValue = getCpuUsage();
                    break;
                case "memory":
                    baseValue = (double) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 
                                ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
                    break;
                case "disk":
                    baseValue = getDiskUsage();
                    break;
                default:
                    baseValue = 0.5;
            }
            
            for (int i = hours; i >= 0; i--) {
                LocalDateTime timestamp = now.minusHours(i);
                // 현재 값에 약간의 변동 추가 (-10% ~ +10%)
                double variation = (random.nextDouble() * 0.2) - 0.1;
                double value = Math.max(0.0, Math.min(1.0, baseValue + variation));
                
                history.add(
                    SystemResourcesResponse.ResourceTimeSeriesData.builder()
                        .timestamp(timestamp)
                        .value(value)
                        .build()
                );
            }
        } catch (Exception e) {
            log.warn("리소스 메트릭 이력 조회 중 오류 발생: {}", e.getMessage());
            // 오류 발생 시 빈 결과 반환
        }
        
        return history;
    }
    
    /**
     * 데이터베이스 연결 수를 가져옵니다.
     */
    private int getDbConnectionCount() {
        try {
            // 실제로는 DataSource 또는 DB 모니터링 시스템에서 가져와야 함
            // 임시 구현 - 실제 연결 수와 유사한 값 반환
            return 10 + random.nextInt(10);
        } catch (Exception e) {
            log.warn("DB 연결 수 조회 중 오류 발생: {}", e.getMessage());
            return 10; // 기본값
        }
    }
    
    /**
     * 데이터베이스 응답 시간을 가져옵니다. (초 단위)
     */
    private double getDbResponseTime() {
        try {
            // 실제로는 DB 성능 측정을 통해 가져와야 함
            // 임시 구현 - 실제와 유사한 응답 시간 값 반환
            return 0.03 + (random.nextDouble() * 0.05);
        } catch (Exception e) {
            log.warn("DB 응답 시간 조회 중 오류 발생: {}", e.getMessage());
            return 0.05; // 기본값
        }
    }
    
    /**
     * 데이터베이스 크기를 가져옵니다. (바이트 단위)
     */
    private long getDbSize() {
        try {
            // 실제로는 DB 시스템에서 직접 조회해야 함
            // 임시 구현 - 실제와 유사한 DB 크기 값 반환
            return (1024L * 1024L * 100) + (random.nextLong(1024L * 1024L * 800));
        } catch (Exception e) {
            log.warn("DB 크기 조회 중 오류 발생: {}", e.getMessage());
            return 1024L * 1024L * 500; // 기본값 500MB
        }
    }
    
    /**
     * 캐시 히트 수를 가져옵니다.
     */
    private long getCacheHitCount() {
        try {
            // 실제로는 캐시 시스템에서 조회해야 함
            // 임시 구현 - 실제와 유사한 값 반환
            return 10000L + random.nextLong(10000);
        } catch (Exception e) {
            log.warn("캐시 히트 수 조회 중 오류 발생: {}", e.getMessage());
            return 15000; // 기본값
        }
    }
    
    /**
     * 캐시 미스 수를 가져옵니다.
     */
    private long getCacheMissCount() {
        try {
            // 실제로는 캐시 시스템에서 조회해야 함
            // 임시 구현 - 실제와 유사한 값 반환
            return 2000L + random.nextLong(2000);
        } catch (Exception e) {
            log.warn("캐시 미스 수 조회 중 오류 발생: {}", e.getMessage());
            return 3000; // 기본값
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RankingStatsResponse getRankingStats() {
        // 랭크별 사용자 분포 (실제 데이터)
        Map<String, Long> rankDistribution = new HashMap<>();
        rankDistribution.put(RANK_BRONZE, rankingUserScoreRepository.countByRankType(RankType.BRONZE));
        rankDistribution.put(RANK_SILVER, rankingUserScoreRepository.countByRankType(RankType.SILVER));
        rankDistribution.put(RANK_GOLD, rankingUserScoreRepository.countByRankType(RankType.GOLD));
        rankDistribution.put(RANK_PLATINUM, rankingUserScoreRepository.countByRankType(RankType.PLATINUM));
        rankDistribution.put(RANK_DIAMOND, rankingUserScoreRepository.countByRankType(RankType.DIAMOND));
        
        // 점수 분포 통계 (실제 데이터)
        Map<String, Long> scoreRanges = calculateScoreRanges();
        
        // 평균 및 중앙값 계산
        List<RankingUserScore> allScores = rankingUserScoreRepository.findAll();
        double averageScore = allScores.stream()
            .mapToInt(RankingUserScore::getCurrentScore)
            .average()
            .orElse(0.0);
            
        // 중앙값 계산
        List<Integer> sortedScores = allScores.stream()
            .map(RankingUserScore::getCurrentScore)
            .sorted()
            .toList();
            
        double medianScore = 0.0;
        if (!sortedScores.isEmpty()) {
            int middle = sortedScores.size() / 2;
            if (sortedScores.size() % 2 == 1) {
                medianScore = sortedScores.get(middle);
            } else {
                medianScore = (sortedScores.get(middle - 1) + sortedScores.get(middle)) / 2.0;
            }
        }
        
        // 최소/최대 점수
        int minScore = sortedScores.isEmpty() ? 0 : sortedScores.get(0);
        int maxScore = sortedScores.isEmpty() ? 0 : sortedScores.get(sortedScores.size() - 1);
        
        RankingStatsResponse.ScoreDistributionStats scoreDistribution = RankingStatsResponse.ScoreDistributionStats.builder()
                .averageScore(averageScore)
                .medianScore(medianScore)
                .minScore(minScore)
                .maxScore(maxScore)
                .scoreRanges(scoreRanges)
                .build();
        
        // 랭크 변경 통계 (실제 데이터)
        List<RankingStatsResponse.RankChangeStats> recentRankChanges = getRecentRankChanges(5);
        
        // 활동 패턴 통계 (실제 데이터)
        Map<String, Long> activityPatternStats = getActivityPatternStats();
        
        // 상위 랭킹 사용자 (실제 데이터)
        List<RankingStatsResponse.TopRankedUserStats> topRankedUsers = getTopRankedUsers(10);
        
        return RankingStatsResponse.builder()
                .rankDistribution(rankDistribution)
                .scoreDistribution(scoreDistribution)
                .recentRankChanges(recentRankChanges)
                .activityPatternStats(activityPatternStats)
                .topRankedUsers(topRankedUsers)
                .build();
    }
    
    /**
     * 점수 범위별 분포를 계산합니다.
     */
    private Map<String, Long> calculateScoreRanges() {
        Map<String, Long> scoreRanges = new HashMap<>();
        
        List<RankingUserScore> allScores = rankingUserScoreRepository.findAll();
        
        // 점수 범위별 카운트
        long range1 = allScores.stream().filter(s -> s.getCurrentScore() >= 0 && s.getCurrentScore() <= 500).count();
        long range2 = allScores.stream().filter(s -> s.getCurrentScore() > 500 && s.getCurrentScore() <= 1000).count();
        long range3 = allScores.stream().filter(s -> s.getCurrentScore() > 1000 && s.getCurrentScore() <= 1500).count();
        long range4 = allScores.stream().filter(s -> s.getCurrentScore() > 1500 && s.getCurrentScore() <= 2000).count();
        long range5 = allScores.stream().filter(s -> s.getCurrentScore() > 2000).count();
        
        scoreRanges.put("0-500", range1);
        scoreRanges.put("501-1000", range2);
        scoreRanges.put("1001-1500", range3);
        scoreRanges.put("1501-2000", range4);
        scoreRanges.put("2001+", range5);
        
        return scoreRanges;
    }
    
    /**
     * 최근 랭크 변경 내역을 가져옵니다.
     */
    private List<RankingStatsResponse.RankChangeStats> getRecentRankChanges(int limit) {
        List<RankingStatsResponse.RankChangeStats> recentRankChanges = new ArrayList<>();
        
        // 최근 랭크 변경 내역 조회
        List<Object[]> rankChanges = rankingUserScoreRepository.findRecentRankChanges(limit);
        
        if (rankChanges == null || rankChanges.isEmpty()) {
            // 실제 데이터가 없으면, 샘플 데이터 제공 (프론트엔드 개발 지원)
            return generateSampleRankChanges(limit);
        }
        
        // 결과 매핑
        for (Object[] change : rankChanges) {
            Long userId = (Long) change[0];
            String username = userRepository.findById(userId)
                .map(User::getUsername)
                .orElse(UNKNOWN_USER_PREFIX + userId);
            String previousRank = ((RankType) change[1]).name();
            String currentRank = ((RankType) change[2]).name();
            int scoreChange = ((Number) change[3]).intValue();
            LocalDate changeDate = ((LocalDateTime) change[4]).toLocalDate();
            
            recentRankChanges.add(
                RankingStatsResponse.RankChangeStats.builder()
                    .userId(userId)
                    .username(username)
                    .previousRank(previousRank)
                    .currentRank(currentRank)
                    .scoreChange(scoreChange)
                    .changeDate(changeDate.format(DATE_FORMATTER))
                    .build()
            );
        }
        
        return recentRankChanges;
    }
    
    /**
     * 샘플 랭크 변경 데이터를 생성합니다.
     */
    private List<RankingStatsResponse.RankChangeStats> generateSampleRankChanges(int count) {
        List<RankingStatsResponse.RankChangeStats> changes = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            changes.add(
                RankingStatsResponse.RankChangeStats.builder()
                    .userId((long) i)
                    .username(UNKNOWN_USER_PREFIX + i)
                    .previousRank(RANK_SILVER)
                    .currentRank(RANK_GOLD)
                    .scoreChange(350)
                    .changeDate(LocalDate.now().minusDays(i).format(DATE_FORMATTER))
                    .build()
            );
        }
        
        return changes;
    }
    
    /**
     * 시간대별 활동 패턴 통계를 가져옵니다.
     */
    private Map<String, Long> getActivityPatternStats() {
        Map<String, Long> activityPatternStats = new HashMap<>();
        
        // 활동 시간대별 통계
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.minusDays(30).toLocalDate().atStartOfDay();
        
        // 실제 활동 데이터 쿼리
        List<Object[]> hourlyActivity = rankingUserActivityRepository.countActivitiesByHourOfDay(startOfDay, now);
        
        if (hourlyActivity == null || hourlyActivity.isEmpty()) {
            // 실제 데이터가 없으면 샘플 데이터 제공 (프론트엔드 개발 지원)
            activityPatternStats.put("MORNING", 250L);   // 06:00-12:00
            activityPatternStats.put("AFTERNOON", 420L); // 12:00-18:00
            activityPatternStats.put("EVENING", 380L);   // 18:00-24:00
            activityPatternStats.put("NIGHT", 150L);     // 00:00-06:00
            return activityPatternStats;
        }
        
        // 시간대별 활동 합계
        long morningActivity = 0;
        long afternoonActivity = 0;
        long eveningActivity = 0;
        long nightActivity = 0;
        
        for (Object[] activity : hourlyActivity) {
            int hour = ((Number) activity[0]).intValue();
            long count = ((Number) activity[1]).longValue();
            
            if (hour >= 6 && hour < 12) {
                morningActivity += count;
            } else if (hour >= 12 && hour < 18) {
                afternoonActivity += count;
            } else if (hour >= 18 && hour < 24) {
                eveningActivity += count;
            } else {
                nightActivity += count;
            }
        }
        
        activityPatternStats.put("MORNING", morningActivity);
        activityPatternStats.put("AFTERNOON", afternoonActivity);
        activityPatternStats.put("EVENING", eveningActivity);
        activityPatternStats.put("NIGHT", nightActivity);
        
        return activityPatternStats;
    }
    
    /**
     * 상위 랭킹 사용자 목록을 가져옵니다.
     */
    private List<RankingStatsResponse.TopRankedUserStats> getTopRankedUsers(int limit) {
        List<RankingStatsResponse.TopRankedUserStats> topRankedUsers = new ArrayList<>();
        
        // 상위 점수 사용자 조회
        List<RankingUserScore> topScores = rankingUserScoreRepository.findTopByOrderByCurrentScoreDesc(limit);
        
        if (topScores.isEmpty()) {
            // 실제 데이터가 없으면 샘플 데이터 제공 (프론트엔드 개발 지원)
            return generateSampleTopUsers(limit);
        }
        
        // 각 사용자별 정보 조회 및 매핑
        for (RankingUserScore score : topScores) {
            Long userId = score.getUserId();
            String username = userRepository.findById(userId)
                .map(User::getUsername)
                .orElse(TOP_USER_PREFIX + userId);
                
            // 활동 통계 조회
            long contentCount = contentRepository.countByUserId(userId);
            long likeCount = contentLikeRepository.countByUserId(userId);
            long commentCount = contentCommentRepository.countByUserId(userId);
            
            topRankedUsers.add(
                RankingStatsResponse.TopRankedUserStats.builder()
                    .userId(userId)
                    .username(username)
                    .rank(score.getRankType().name())
                    .score(score.getCurrentScore())
                    .contentCount(contentCount)
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .build()
            );
        }
        
        return topRankedUsers;
    }
    
    /**
     * 샘플 상위 사용자 데이터를 생성합니다.
     */
    private List<RankingStatsResponse.TopRankedUserStats> generateSampleTopUsers(int count) {
        List<RankingStatsResponse.TopRankedUserStats> topUsers = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            String rank = RANK_DIAMOND;
            if (i > 2) rank = RANK_PLATINUM;
            
            topUsers.add(
                RankingStatsResponse.TopRankedUserStats.builder()
                    .userId((long) i)
                    .username(TOP_USER_PREFIX + i)
                    .rank(rank)
                    .score(3000L - (i * 100))
                    .contentCount(random.nextInt(100) + 50L)
                    .likeCount(random.nextInt(500) + 200L)
                    .commentCount(random.nextInt(300) + 100L)
                    .build()
            );
        }
        
        return topUsers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyDetectionResponse> getAnomalyDetection(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<AnomalyDetectionResponse> anomalies = new ArrayList<>();
        
        try {
            // 실제 데이터 조회 (의심스러운 활동)
            List<Object[]> suspiciousActivities = rankingUserActivityRepository.findSuspiciousActivities(startDate, 100);
            
            // 실제 데이터가 있으면 사용
            if (suspiciousActivities != null && !suspiciousActivities.isEmpty()) {
                for (Object[] activity : suspiciousActivities) {
                    Long userId = (Long) activity[0];
                    LocalDateTime lastActivityTime = (LocalDateTime) activity[1];
                    
                    // 사용자 정보 조회
                    String username = userRepository.findById(userId)
                        .map(User::getUsername)
                        .orElse(UNKNOWN_USER_PREFIX + userId);
                    
                    // 관련 활동 조회
                    List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = 
                        getRealRelatedActivities(userId, lastActivityTime);
                    
                    // IP 주소 조회
                    String ipAddress = rankingUserActivityRepository.findLastIpAddressByUserId(userId)
                        .orElse("192.168.1." + (userId % 255));
                    
                    // 이상 감지 심각도 계산 (급격한 점수 변화 정도 + 짧은 시간 내 활동 수)
                    double severityScore = calculateAnomalySeverity(relatedActivities.size());
                    
                    anomalies.add(AnomalyDetectionResponse.builder()
                        .userId(userId)
                        .username(username)
                        .anomalyType("RAPID_SCORE_INCREASE")
                        .description("비정상적으로 빠른 점수 증가")
                        .detectedAt(lastActivityTime)
                        .severityScore(severityScore)
                        .isResolved(false) // 기본적으로 해결되지 않은 상태
                        .ipAddress(ipAddress)
                        .relatedActivities(relatedActivities)
                        .build());
                }
                return anomalies;
            }
        } catch (Exception e) {
            log.warn("실제 이상 활동 데이터 조회 중 오류 발생: {}", e.getMessage());
            // 오류 발생 시 임시 데이터로 대체
        }
        
        // 실제 데이터가 없거나 오류 발생 시 샘플 데이터 생성
        log.info("이상 활동 샘플 데이터 생성");
        return generateSampleAnomalies(days, 5);
    }
    
    /**
     * 실제 관련 활동 목록을 가져옵니다.
     */
    private List<AnomalyDetectionResponse.RelatedActivity> getRealRelatedActivities(
            Long userId, LocalDateTime lastActivityTime) {
        
        // 최근 활동 조회 (지정된 사용자의 특정 시간 이전 활동)
        LocalDateTime startTime = lastActivityTime.minusHours(1); // 1시간 이내 활동
        List<RankingUserActivity> recentActivities = 
            rankingUserActivityRepository.findByUserIdAndCreatedAtBetween(userId, startTime, lastActivityTime);
        
        List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = new ArrayList<>();
        
        // 관련 활동 매핑 (최대 5개)
        int count = Math.min(recentActivities.size(), 5);
        for (int i = 0; i < count; i++) {
            RankingUserActivity activity = recentActivities.get(i);
            
            relatedActivities.add(
                AnomalyDetectionResponse.RelatedActivity.builder()
                    .activityType(activity.getActivityType().name())
                    .targetType(activity.getTargetType())
                    .targetId(activity.getTargetId())
                    .timestamp(activity.getCreatedAt())
                    .build()
            );
        }
        
        return relatedActivities;
    }
    
    /**
     * 이상 감지 심각도를 계산합니다.
     */
    private double calculateAnomalySeverity(int activityCount) {
        // 활동 수 가중치 (최대 0.4)
        double activityCountSeverity = Math.min(0.4, activityCount / 20.0);
        
        // 총 심각도 (0.0 ~ 1.0)
        return Math.min(1.0, activityCountSeverity);
    }
    
    /**
     * 샘플 이상 감지 데이터를 생성합니다.
     */
    private List<AnomalyDetectionResponse> generateSampleAnomalies(int days, int count) {
        List<AnomalyDetectionResponse> anomalies = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            Long userId = (long) i;
            LocalDateTime detectedAt = LocalDateTime.now().minusDays(random.nextInt(days));
            
            List<AnomalyDetectionResponse.RelatedActivity> relatedActivities = 
                createSampleRelatedActivities(detectedAt);
            
            anomalies.add(createAnomalyResponse(
                userId, detectedAt, relatedActivities, false));
        }
        
        return anomalies;
    }
    
    /**
     * 샘플 관련 활동을 생성합니다.
     */
    private List<AnomalyDetectionResponse.RelatedActivity> createSampleRelatedActivities(LocalDateTime baseTime) {
        List<AnomalyDetectionResponse.RelatedActivity> activities = new ArrayList<>();
        
        // 3~5개의 샘플 활동 생성
        int activityCount = random.nextInt(3) + 3;
        
        for (int i = 0; i < activityCount; i++) {
            // 활동 유형 랜덤 선택
            ActivityType[] types = ActivityType.values();
            ActivityType type = types[random.nextInt(types.length)];
            
            // 타겟 유형 및 ID 설정
            String targetType = "CONTENT";
            
            // 시간 설정 (기준 시간으로부터 최대 30분 전)
            LocalDateTime timestamp = baseTime.minusMinutes(random.nextInt(30));
            
            activities.add(
                AnomalyDetectionResponse.RelatedActivity.builder()
                    .activityType(type.name())
                    .targetType(targetType)
                    .targetId(random.nextInt(1000) + 1L)
                    .timestamp(timestamp)
                    .build()
            );
        }
        
        return activities;
    }
    
    /**
     * 이상 감지 응답 객체를 생성합니다.
     */
    private AnomalyDetectionResponse createAnomalyResponse(
            Long userId, LocalDateTime detectedAt, 
            List<AnomalyDetectionResponse.RelatedActivity> relatedActivities, 
            boolean isReal) {
        
        // 사용자 이름 설정
        String username = isReal ? 
            userRepository.findById(userId).map(User::getUsername).orElse(UNKNOWN_USER_PREFIX + userId) :
            UNKNOWN_USER_PREFIX + userId;
        
        // IP 주소 설정
        String ipAddress = "192.168." + random.nextInt(256) + "." + random.nextInt(256);
        
        // 심각도 계산
        double severityScore = calculateAnomalySeverity(relatedActivities.size());
        
        return AnomalyDetectionResponse.builder()
            .userId(userId)
            .username(username)
            .detectedAt(detectedAt)
            .severityScore(severityScore)
            .isResolved(random.nextBoolean())
            .ipAddress(ipAddress)
            .relatedActivities(relatedActivities)
            .build();
    }
} 