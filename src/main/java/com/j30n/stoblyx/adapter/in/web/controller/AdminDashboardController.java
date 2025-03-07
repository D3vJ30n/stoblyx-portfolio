package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 대시보드 페이지를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    /**
     * 관리자 대시보드 메인 페이지를 반환합니다.
     *
     * @return 대시보드 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<String>> dashboard() {
        try {
            log.info("관리자 대시보드 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("관리자 대시보드에 오신 것을 환영합니다.", "admin/dashboard"));
        } catch (Exception e) {
            log.error("관리자 대시보드 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자 관리 페이지를 반환합니다.
     *
     * @return 사용자 관리 응답
     */
    @GetMapping("/users-view")
    public ResponseEntity<ApiResponse<String>> userManagement() {
        try {
            log.info("사용자 관리 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("사용자 관리 페이지에 오신 것을 환영합니다.", "admin/users"));
        } catch (Exception e) {
            log.error("사용자 관리 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 책 관리 페이지를 반환합니다.
     *
     * @return 책 관리 응답
     */
    @GetMapping("/books-view")
    public ResponseEntity<ApiResponse<String>> bookManagement() {
        try {
            log.info("책 관리 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("책 관리 페이지에 오신 것을 환영합니다.", "admin/books"));
        } catch (Exception e) {
            log.error("책 관리 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 콘텐츠 관리 페이지를 반환합니다.
     *
     * @return 콘텐츠 관리 응답
     */
    @GetMapping("/contents-view")
    public ResponseEntity<ApiResponse<String>> contentManagement() {
        try {
            log.info("콘텐츠 관리 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("콘텐츠 관리 페이지에 오신 것을 환영합니다.", "admin/contents"));
        } catch (Exception e) {
            log.error("콘텐츠 관리 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 통계 대시보드 페이지를 반환합니다.
     *
     * @return 통계 대시보드 응답
     */
    @GetMapping("/statistics-view")
    public ResponseEntity<ApiResponse<String>> statisticsDashboard() {
        try {
            log.info("통계 대시보드 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("통계 대시보드에 오신 것을 환영합니다.", "admin/statistics"));
        } catch (Exception e) {
            log.error("통계 대시보드 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 콘텐츠 생성 통계 페이지를 반환합니다.
     *
     * @return 콘텐츠 생성 통계 응답
     */
    @GetMapping("/statistics-view/content")
    public ResponseEntity<ApiResponse<String>> contentStatistics() {
        try {
            log.info("콘텐츠 생성 통계 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("콘텐츠 생성 통계 페이지에 오신 것을 환영합니다.", "admin/statistics/content"));
        } catch (Exception e) {
            log.error("콘텐츠 생성 통계 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 사용자 활동 통계 페이지를 반환합니다.
     *
     * @return 사용자 활동 통계 응답
     */
    @GetMapping("/statistics-view/user-activity")
    public ResponseEntity<ApiResponse<String>> userActivityStatistics() {
        try {
            log.info("사용자 활동 통계 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("사용자 활동 통계 페이지에 오신 것을 환영합니다.", "admin/statistics/user-activity"));
        } catch (Exception e) {
            log.error("사용자 활동 통계 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 시스템 리소스 모니터링 페이지를 반환합니다.
     *
     * @return 시스템 리소스 모니터링 응답
     */
    @GetMapping("/statistics-view/system-resources")
    public ResponseEntity<ApiResponse<String>> systemResourcesMonitoring() {
        try {
            log.info("시스템 리소스 모니터링 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("시스템 리소스 모니터링 페이지에 오신 것을 환영합니다.", "admin/statistics/system-resources"));
        } catch (Exception e) {
            log.error("시스템 리소스 모니터링 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 랭킹 시스템 통계 페이지를 반환합니다.
     *
     * @return 랭킹 시스템 통계 응답
     */
    @GetMapping("/statistics-view/ranking")
    public ResponseEntity<ApiResponse<String>> rankingStatistics() {
        try {
            log.info("랭킹 시스템 통계 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("랭킹 시스템 통계 페이지에 오신 것을 환영합니다.", "admin/statistics/ranking"));
        } catch (Exception e) {
            log.error("랭킹 시스템 통계 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 이상 활동 탐지 페이지를 반환합니다.
     *
     * @return 이상 활동 탐지 응답
     */
    @GetMapping("/statistics-view/anomaly-detection")
    public ResponseEntity<ApiResponse<String>> anomalyDetection() {
        try {
            log.info("이상 활동 탐지 페이지 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("이상 활동 탐지 페이지에 오신 것을 환영합니다.", "admin/statistics/anomaly-detection"));
        } catch (Exception e) {
            log.error("이상 활동 탐지 페이지 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 