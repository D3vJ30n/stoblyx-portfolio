package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingActivityResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingScoreAdjustRequest;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingScoreResponse;
import com.j30n.stoblyx.adapter.in.web.dto.admin.AdminRankingStatisticsResponse;
import com.j30n.stoblyx.application.port.in.admin.AdminRankingUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자의 랭킹 시스템 관리 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/admin/ranking")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminRankingController {

    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    
    private final AdminRankingUseCase adminRankingUseCase;
    
    public AdminRankingController(AdminRankingUseCase adminRankingUseCase) {
        this.adminRankingUseCase = adminRankingUseCase;
    }

    /**
     * 의심스러운 활동이 있는 사용자 목록 조회
     *
     * @param threshold 점수 변화 임계값
     * @return 의심스러운 활동이 있는 사용자 목록
     */
    @GetMapping("/suspicious")
    public ResponseEntity<ApiResponse<List<AdminRankingScoreResponse>>> getSuspiciousUsers(
            @RequestParam(defaultValue = "100") int threshold) {
        try {
            List<AdminRankingScoreResponse> users = adminRankingUseCase.findUsersWithSuspiciousActivity(threshold);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "의심스러운 활동이 있는 사용자 목록을 조회했습니다.", users));
        } catch (Exception e) {
            log.error("의심스러운 활동이 있는 사용자 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "의심스러운 활동이 있는 사용자 목록 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 특정 기간 내 비정상적인 활동 패턴 조회
     *
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @param activityThreshold 활동 횟수 임계값
     * @return 비정상적인 활동 패턴 목록
     */
    @GetMapping("/abnormal-activities")
    public ResponseEntity<ApiResponse<List<AdminRankingActivityResponse>>> getAbnormalActivities(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "50") int activityThreshold) {
        try {
            List<AdminRankingActivityResponse> activities = adminRankingUseCase.findAbnormalActivityPatterns(
                    startDate, endDate, activityThreshold);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "비정상적인 활동 패턴을 조회했습니다.", activities));
        } catch (Exception e) {
            log.error("비정상적인 활동 패턴 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "비정상적인 활동 패턴 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 특정 IP 주소의 활동 내역 조회
     *
     * @param ipAddress IP 주소
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 활동 내역 목록
     */
    @GetMapping("/activities/ip/{ipAddress}")
    public ResponseEntity<ApiResponse<List<AdminRankingActivityResponse>>> getActivitiesByIpAddress(
            @PathVariable String ipAddress,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<AdminRankingActivityResponse> activities = adminRankingUseCase.findActivitiesByIpAddress(
                    ipAddress, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "IP 주소별 활동 내역을 조회했습니다.", activities));
        } catch (Exception e) {
            log.error("IP 주소별 활동 내역 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "IP 주소별 활동 내역 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 사용자 점수 수동 조정
     *
     * @param userId 사용자 ID
     * @param request 점수 조정 요청 정보
     * @return 조정된 사용자 점수 정보
     */
    @PostMapping("/users/{userId}/adjust-score")
    public ResponseEntity<ApiResponse<AdminRankingScoreResponse>> adjustUserScore(
            @PathVariable Long userId,
            @Valid @RequestBody AdminRankingScoreAdjustRequest request) {
        try {
            AdminRankingScoreResponse score = adminRankingUseCase.adjustUserScore(
                    userId, request.scoreAdjustment(), request.reason());
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "사용자 점수를 조정했습니다.", score));
        } catch (IllegalArgumentException e) {
            log.error("사용자 점수 조정 중 유효성 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ERROR, e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 점수 조정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "사용자 점수 조정 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 사용자 계정 정지 처리
     *
     * @param userId 사용자 ID
     * @param reason 정지 사유
     * @return 정지된 사용자 점수 정보
     */
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<ApiResponse<AdminRankingScoreResponse>> suspendUserAccount(
            @PathVariable Long userId,
            @RequestParam String reason) {
        try {
            AdminRankingScoreResponse score = adminRankingUseCase.suspendUserAccount(userId, reason);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "사용자 계정을 정지했습니다.", score));
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 정지 중 유효성 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ERROR, e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 계정 정지 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "사용자 계정 정지 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 사용자 계정 정지 해제
     *
     * @param userId 사용자 ID
     * @return 정지 해제된 사용자 점수 정보
     */
    @PostMapping("/users/{userId}/unsuspend")
    public ResponseEntity<ApiResponse<AdminRankingScoreResponse>> unsuspendUserAccount(
            @PathVariable Long userId) {
        try {
            AdminRankingScoreResponse score = adminRankingUseCase.unsuspendUserAccount(userId);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "사용자 계정 정지를 해제했습니다.", score));
        } catch (IllegalArgumentException e) {
            log.error("사용자 계정 정지 해제 중 유효성 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ERROR, e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 계정 정지 해제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "사용자 계정 정지 해제 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 랭킹 시스템 통계 조회
     *
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 랭킹 시스템 통계 정보
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<AdminRankingStatisticsResponse>> getRankingStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            AdminRankingStatisticsResponse statistics = adminRankingUseCase.getRankingStatistics(startDate, endDate);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "랭킹 시스템 통계를 조회했습니다.", statistics));
        } catch (Exception e) {
            log.error("랭킹 시스템 통계 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "랭킹 시스템 통계 조회 중 오류가 발생했습니다.", null));
        }
    }

    /**
     * 랭킹 시스템 설정 업데이트
     *
     * @param settingKey 설정 키
     * @param settingValue 설정 값
     * @return 업데이트 성공 여부
     */
    @PostMapping("/settings/{settingKey}")
    public ResponseEntity<ApiResponse<Boolean>> updateRankingSystemSetting(
            @PathVariable String settingKey,
            @RequestParam String settingValue) {
        try {
            boolean updated = adminRankingUseCase.updateRankingSystemSetting(settingKey, settingValue);
            return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "랭킹 시스템 설정을 업데이트했습니다.", updated));
        } catch (IllegalArgumentException e) {
            log.error("랭킹 시스템 설정 업데이트 중 유효성 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ERROR, e.getMessage(), null));
        } catch (Exception e) {
            log.error("랭킹 시스템 설정 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ERROR, "랭킹 시스템 설정 업데이트 중 오류가 발생했습니다.", null));
        }
    }
} 