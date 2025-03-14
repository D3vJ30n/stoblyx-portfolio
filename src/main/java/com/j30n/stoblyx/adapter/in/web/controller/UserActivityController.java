package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ranking.UserActivityResponse;
import com.j30n.stoblyx.application.port.in.ranking.RankingUserActivityUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.enums.ActivityType;
import com.j30n.stoblyx.domain.model.RankingUserActivity;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 활동 내역 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserActivityController {
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";
    private static final String ERROR_USER_NOT_AUTHENTICATED = "인증된 사용자 정보를 찾을 수 없습니다.";

    private final RankingUserActivityUseCase rankingUserActivityUseCase;

    /**
     * 사용자 활동 내역 조회 - 인증된 사용자
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자 활동 내역 목록
     */
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivities(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam(required = false) Long userId
    ) {
        try {
            // userId가 제공된 경우 해당 사용자의 활동 내역 조회
            if (userId != null) {
                log.info("사용자 활동 내역 조회 요청 (ID 지정): userId={}", userId);
                return getUserActivitiesById(userId);
            }
            
            // 인증된 사용자 정보 확인
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 활동 내역 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }
            
            log.info("인증된 사용자 활동 내역 조회 요청: userId={}", userPrincipal.getId());
            List<RankingUserActivity> activities = rankingUserActivityUseCase.getUserActivities(userPrincipal.getId());
            List<UserActivityResponse> response = activities.stream()
                .map(UserActivityResponse::fromEntity)
                .toList();
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "사용자 활동 내역을 성공적으로 조회했습니다.", response)
            );
        } catch (Exception e) {
            log.error("사용자 활동 내역 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "활동 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * 사용자 ID로 활동 내역 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 활동 내역 목록
     */
    @GetMapping("/{userId}/activities")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivitiesById(
        @PathVariable Long userId
    ) {
        try {
            log.info("사용자 ID로 활동 내역 조회 요청: userId={}", userId);
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, "유효하지 않은 사용자 ID입니다.", null)
                );
            }
            
            List<RankingUserActivity> activities = rankingUserActivityUseCase.getUserActivities(userId);
            List<UserActivityResponse> response = activities.stream()
                .map(UserActivityResponse::fromEntity)
                .toList();
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "사용자 활동 내역을 성공적으로 조회했습니다.", response)
            );
        } catch (Exception e) {
            log.error("사용자 ID로 활동 내역 조회 중 오류 발생: userId={}", userId, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "활동 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * 사용자 활동 유형별 내역 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param activityType 활동 유형
     * @return 활동 유형별 내역 목록
     */
    @GetMapping("/activities/type/{activityType}")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivitiesByType(
        @CurrentUser UserPrincipal userPrincipal,
        @PathVariable ActivityType activityType,
        @RequestParam(required = false) Long userId
    ) {
        try {
            // userId가 제공된 경우 해당 사용자의 활동 내역 조회
            if (userId != null && userId > 0) {
                log.info("사용자 활동 유형별 내역 조회 요청 (ID 지정): userId={}, activityType={}", userId, activityType);
                return getUserActivitiesByTypeAndUserId(userId, activityType);
            }
            
            // 인증된 사용자 정보 확인
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 활동 유형별 내역 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }
            
            log.info("인증된 사용자 활동 유형별 내역 조회 요청: userId={}, activityType={}", 
                userPrincipal.getId(), activityType);
            List<RankingUserActivity> activities = rankingUserActivityUseCase.getUserActivitiesByType(
                userPrincipal.getId(), activityType);
            List<UserActivityResponse> response = activities.stream()
                .map(UserActivityResponse::fromEntity)
                .toList();
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, activityType.name() + " 유형의 활동 내역을 성공적으로 조회했습니다.", response)
            );
        } catch (Exception e) {
            log.error("활동 유형별 내역 조회 중 오류 발생: activityType={}", activityType, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "활동 유형별 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * 사용자 ID로 활동 유형별 내역 조회
     *
     * @param userId 사용자 ID
     * @param activityType 활동 유형
     * @return 활동 유형별 내역 목록
     */
    @GetMapping("/{userId}/activities/type/{activityType}")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivitiesByTypeAndUserId(
        @PathVariable Long userId,
        @PathVariable ActivityType activityType
    ) {
        try {
            log.info("사용자 ID로 활동 유형별 내역 조회 요청: userId={}, activityType={}", userId, activityType);
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, "유효하지 않은 사용자 ID입니다.", null)
                );
            }
            
            List<RankingUserActivity> activities = rankingUserActivityUseCase.getUserActivitiesByType(
                userId, activityType);
            List<UserActivityResponse> response = activities.stream()
                .map(UserActivityResponse::fromEntity)
                .toList();
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, 
                    activityType.name() + " 유형의 활동 내역을 성공적으로 조회했습니다.", response)
            );
        } catch (Exception e) {
            log.error("사용자 ID로 활동 유형별 내역 조회 중 오류 발생: userId={}, activityType={}", 
                userId, activityType, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "활동 유형별 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * 특정 기간 내 사용자 활동 내역 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 기간 내 활동 내역 목록
     */
    @GetMapping("/activities/period")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivitiesByPeriod(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @RequestParam(required = false) Long userId
    ) {
        try {
            // userId가 제공된 경우 해당 사용자의 활동 내역 조회
            if (userId != null && userId > 0) {
                log.info("사용자 기간별 활동 내역 조회 요청 (ID 지정): userId={}, startDate={}, endDate={}", 
                    userId, startDate, endDate);
                return getUserActivitiesByPeriodAndUserId(userId, startDate, endDate);
            }
            
            // 인증된 사용자 정보 확인
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 기간별 활동 내역 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }
            
            log.info("인증된 사용자 기간별 내역 조회 요청: userId={}, startDate={}, endDate={}", 
                userPrincipal.getId(), startDate, endDate);
            List<RankingUserActivity> activities = rankingUserActivityUseCase.getUserActivitiesByPeriod(
                userPrincipal.getId(), startDate, endDate);
            List<UserActivityResponse> response = activities.stream()
                .map(UserActivityResponse::fromEntity)
                .toList();
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "해당 기간 내 활동 내역을 성공적으로 조회했습니다.", response)
            );
        } catch (Exception e) {
            log.error("기간별 활동 내역 조회 중 오류 발생: startDate={}, endDate={}", startDate, endDate, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "기간별 활동 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
    
    /**
     * 특정 기간 내 사용자 활동 내역 조회 (사용자 ID 기반)
     *
     * @param userId 사용자 ID
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 기간 내 활동 내역 목록
     */
    @GetMapping("/{userId}/activities/period")
    public ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserActivitiesByPeriodAndUserId(
        @PathVariable Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            log.info("사용자 ID로 기간별 활동 내역 조회 요청: userId={}, startDate={}, endDate={}", 
                userId, startDate, endDate);
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, "유효하지 않은 사용자 ID입니다.", null)
                );
            }
            
            List<RankingUserActivity> activities = rankingUserActivityUseCase.getUserActivitiesByPeriod(
                userId, startDate, endDate);
            List<UserActivityResponse> response = activities.stream()
                .map(UserActivityResponse::fromEntity)
                .toList();
                
            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "해당 기간 내 활동 내역을 성공적으로 조회했습니다.", response)
            );
        } catch (Exception e) {
            log.error("사용자 ID로 기간별 활동 내역 조회 중 오류 발생: userId={}, startDate={}, endDate={}", 
                userId, startDate, endDate, e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "기간별 활동 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
} 