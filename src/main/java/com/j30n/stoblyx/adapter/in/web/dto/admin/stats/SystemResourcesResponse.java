package com.j30n.stoblyx.adapter.in.web.dto.admin.stats;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 시스템 리소스 사용량 응답 객체
 */
@Getter
@Builder
public class SystemResourcesResponse {

    // 현재 시스템 상태
    private final LocalDateTime timestamp;
    private final double cpuUsage;
    private final double memoryUsage;
    private final double diskUsage;
    private final long heapMemoryUsed;
    private final long heapMemoryMax;
    private final int threadCount;

    // 시스템 리소스 사용량 추이
    private final List<ResourceTimeSeriesData> cpuUsageHistory;
    private final List<ResourceTimeSeriesData> memoryUsageHistory;
    private final List<ResourceTimeSeriesData> diskUsageHistory;

    // 데이터베이스 통계
    private final long dbConnectionCount;
    private final double dbResponseTime;
    private final long dbSize;

    // 캐시 통계
    private final long cacheHitCount;
    private final long cacheMissCount;
    private final double cacheHitRatio;

    /**
     * 리소스 시계열 데이터
     */
    @Getter
    @Builder
    public static class ResourceTimeSeriesData {
        private final LocalDateTime timestamp;
        private final double value;
    }
} 