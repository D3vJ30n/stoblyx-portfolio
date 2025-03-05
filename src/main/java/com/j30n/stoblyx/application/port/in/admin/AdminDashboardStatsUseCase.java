package com.j30n.stoblyx.application.port.in.admin;

import com.j30n.stoblyx.adapter.in.web.dto.admin.stats.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 관리자 대시보드 통계 UseCase 인터페이스
 */
public interface AdminDashboardStatsUseCase {

    /**
     * 대시보드 요약 통계를 조회합니다.
     *
     * @return 대시보드 요약 통계
     */
    DashboardSummaryResponse getDashboardSummary();

    /**
     * 콘텐츠 생성 통계를 조회합니다.
     *
     * @param period    기간 (daily, weekly, monthly)
     * @param startDate 시작일 (선택)
     * @param endDate   종료일 (선택)
     * @return 콘텐츠 생성 통계
     */
    ContentStatsResponse getContentStats(String period, LocalDate startDate, LocalDate endDate);

    /**
     * 사용자 활동 통계를 조회합니다.
     *
     * @param period    기간 (daily, weekly, monthly)
     * @param startDate 시작일 (선택)
     * @param endDate   종료일 (선택)
     * @return 사용자 활동 통계
     */
    UserActivityStatsResponse getUserActivityStats(String period, LocalDate startDate, LocalDate endDate);

    /**
     * 시스템 리소스 사용량을 조회합니다.
     *
     * @return 시스템 리소스 사용량
     */
    SystemResourcesResponse getSystemResources();

    /**
     * 랭킹 시스템 통계를 조회합니다.
     *
     * @return 랭킹 시스템 통계
     */
    RankingStatsResponse getRankingStats();

    /**
     * 이상 활동 탐지 결과를 조회합니다.
     *
     * @param days 최근 일수
     * @return 이상 활동 탐지 결과
     */
    List<AnomalyDetectionResponse> getAnomalyDetection(int days);
} 