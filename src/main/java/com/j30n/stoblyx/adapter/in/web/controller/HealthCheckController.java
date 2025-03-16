package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 헬스 체크 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthEndpoint healthEndpoint;

    /**
     * 간단한 헬스 체크 엔드포인트
     * 
     * @return 헬스 체크 결과
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        try {
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("status", "UP");
            healthData.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "시스템이 정상 작동 중입니다.", healthData));
        } catch (Exception e) {
            log.error("헬스 체크 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "시스템 상태 확인 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 상세 헬스 체크 엔드포인트
     * Spring Boot Actuator의 헬스 정보를 활용
     * 
     * @return 상세 헬스 체크 결과
     */
    @GetMapping("/details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> detailedHealthCheck() {
        try {
            HealthComponent health = healthEndpoint.health();
            Status status = health.getStatus();
            
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("status", status.getCode());
            healthData.put("timestamp", System.currentTimeMillis());
            healthData.put("details", health);
            
            boolean isUp = Status.UP.equals(status);
            HttpStatus httpStatus = isUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
            String message = isUp ? "시스템이 정상 작동 중입니다." : "시스템에 문제가 발생했습니다.";
            
            return ResponseEntity.status(httpStatus)
                    .body(new ApiResponse<>(isUp ? "SUCCESS" : "ERROR", message, healthData));
        } catch (Exception e) {
            log.error("상세 헬스 체크 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "시스템 상태 확인 중 오류가 발생했습니다.", null));
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Application is running");
    }
} 