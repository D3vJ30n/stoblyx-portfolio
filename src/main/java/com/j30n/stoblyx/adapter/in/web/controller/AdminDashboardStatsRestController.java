package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.admin.stats.*;
import com.j30n.stoblyx.application.port.in.admin.AdminDashboardStatsUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 관리자 대시보드 통계 REST API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardStatsRestController {

    private final AdminDashboardStatsUseCase dashboardStatsUseCase;

    /**
     * 대시보드 요약 통계를 조회합니다.
     *
     * @return 대시보드 요약 통계
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        try {
            DashboardSummaryResponse summary = dashboardStatsUseCase.getDashboardSummary();
            return ResponseEntity.ok(ApiResponse.success("대시보드 요약 통계 조회에 성공했습니다.", summary));
        } catch (Exception e) {
            log.error("대시보드 요약 통계 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("대시보드 요약 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 콘텐츠 생성 통계를 조회합니다.
     *
     * @param period    기간 (daily, weekly, monthly)
     * @param startDate 시작일 (선택)
     * @param endDate   종료일 (선택)
     * @return 콘텐츠 생성 통계
     */
    @GetMapping("/content")
    public ResponseEntity<ApiResponse<ContentStatsResponse>> getContentStats(
        @RequestParam String period,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            ContentStatsResponse stats = dashboardStatsUseCase.getContentStats(period, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("콘텐츠 생성 통계 조회에 성공했습니다.", stats));
        } catch (Exception e) {
            log.error("콘텐츠 생성 통계 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("콘텐츠 생성 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자 활동 통계를 조회합니다.
     *
     * @param period    기간 (daily, weekly, monthly)
     * @param startDate 시작일 (선택)
     * @param endDate   종료일 (선택)
     * @return 사용자 활동 통계
     */
    @GetMapping("/user-activity")
    public ResponseEntity<ApiResponse<UserActivityStatsResponse>> getUserActivityStats(
        @RequestParam String period,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            UserActivityStatsResponse stats = dashboardStatsUseCase.getUserActivityStats(period, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("사용자 활동 통계 조회에 성공했습니다.", stats));
        } catch (Exception e) {
            log.error("사용자 활동 통계 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("사용자 활동 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 시스템 리소스 사용량을 조회합니다.
     *
     * @return 시스템 리소스 사용량
     */
    @GetMapping("/system-resources")
    public ResponseEntity<ApiResponse<SystemResourcesResponse>> getSystemResources() {
        try {
            SystemResourcesResponse resources = dashboardStatsUseCase.getSystemResources();
            return ResponseEntity.ok(ApiResponse.success("시스템 리소스 사용량 조회에 성공했습니다.", resources));
        } catch (Exception e) {
            log.error("시스템 리소스 사용량 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("시스템 리소스 사용량 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 랭킹 시스템 통계를 조회합니다.
     *
     * @return 랭킹 시스템 통계
     */
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<RankingStatsResponse>> getRankingStats() {
        try {
            RankingStatsResponse stats = dashboardStatsUseCase.getRankingStats();
            return ResponseEntity.ok(ApiResponse.success("랭킹 시스템 통계 조회에 성공했습니다.", stats));
        } catch (Exception e) {
            log.error("랭킹 시스템 통계 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("랭킹 시스템 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 이상 활동 탐지 결과를 조회합니다.
     *
     * @param days 최근 일수 (기본값: 7)
     * @return 이상 활동 탐지 결과
     */
    @GetMapping("/anomaly-detection")
    public ResponseEntity<ApiResponse<List<AnomalyDetectionResponse>>> getAnomalyDetection(
        @RequestParam(defaultValue = "7") int days
    ) {
        try {
            List<AnomalyDetectionResponse> anomalies = dashboardStatsUseCase.getAnomalyDetection(days);
            return ResponseEntity.ok(ApiResponse.success("이상 활동 탐지 결과 조회에 성공했습니다.", anomalies));
        } catch (Exception e) {
            log.error("이상 활동 탐지 결과 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("이상 활동 탐지 결과 조회에 실패했습니다: " + e.getMessage()));
        }
    }
} 