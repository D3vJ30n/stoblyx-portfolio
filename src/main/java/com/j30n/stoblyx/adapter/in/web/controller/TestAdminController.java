package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 관리자 대시보드 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin-test")
@RequiredArgsConstructor
public class TestAdminController {

    /**
     * 관리자 대시보드 테스트 엔드포인트
     *
     * @return API 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        try {
            log.info("관리자 대시보드 요청을 처리합니다.");
            return ResponseEntity.ok(ApiResponse.success("관리자 대시보드에 오신 것을 환영합니다.", "테스트 버전"));
        } catch (Exception e) {
            log.error("관리자 대시보드 처리 중 오류 발생", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 