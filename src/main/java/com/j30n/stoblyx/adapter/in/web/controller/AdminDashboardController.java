package com.j30n.stoblyx.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 대시보드 페이지를 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    /**
     * 관리자 대시보드 메인 페이지를 반환합니다.
     *
     * @return 대시보드 뷰 이름
     */
    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    /**
     * 사용자 관리 페이지를 반환합니다.
     *
     * @return 사용자 관리 뷰 이름
     */
    @GetMapping("/users")
    public String userManagement() {
        return "admin/users";
    }

    /**
     * 책 관리 페이지를 반환합니다.
     *
     * @return 책 관리 뷰 이름
     */
    @GetMapping("/books")
    public String bookManagement() {
        return "admin/books";
    }
    
    /**
     * 콘텐츠 관리 페이지를 반환합니다.
     *
     * @return 콘텐츠 관리 뷰 이름
     */
    @GetMapping("/contents")
    public String contentManagement() {
        return "admin/contents";
    }
    
    /**
     * 통계 대시보드 페이지를 반환합니다.
     *
     * @return 통계 대시보드 뷰 이름
     */
    @GetMapping("/statistics")
    public String statisticsDashboard() {
        return "admin/statistics";
    }
    
    /**
     * 콘텐츠 생성 통계 페이지를 반환합니다.
     *
     * @return 콘텐츠 생성 통계 뷰 이름
     */
    @GetMapping("/statistics/content")
    public String contentStatistics() {
        return "admin/statistics/content";
    }
    
    /**
     * 사용자 활동 통계 페이지를 반환합니다.
     *
     * @return 사용자 활동 통계 뷰 이름
     */
    @GetMapping("/statistics/user-activity")
    public String userActivityStatistics() {
        return "admin/statistics/user-activity";
    }
    
    /**
     * 시스템 리소스 모니터링 페이지를 반환합니다.
     *
     * @return 시스템 리소스 모니터링 뷰 이름
     */
    @GetMapping("/statistics/system-resources")
    public String systemResourcesMonitoring() {
        return "admin/statistics/system-resources";
    }
    
    /**
     * 랭킹 시스템 통계 페이지를 반환합니다.
     *
     * @return 랭킹 시스템 통계 뷰 이름
     */
    @GetMapping("/statistics/ranking")
    public String rankingStatistics() {
        return "admin/statistics/ranking";
    }
    
    /**
     * 이상 활동 탐지 페이지를 반환합니다.
     *
     * @return 이상 활동 탐지 뷰 이름
     */
    @GetMapping("/statistics/anomaly-detection")
    public String anomalyDetection() {
        return "admin/statistics/anomaly-detection";
    }
} 