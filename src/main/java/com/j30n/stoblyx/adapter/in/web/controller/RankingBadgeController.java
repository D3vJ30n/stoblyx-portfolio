package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.badge.RankingBadgeResponse;
import com.j30n.stoblyx.application.port.in.ranking.RankingBadgeUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 배지 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
public class RankingBadgeController {

    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";
    private static final String ERROR_USER_NOT_AUTHENTICATED = "인증된 사용자 정보를 찾을 수 없습니다.";

    private final RankingBadgeUseCase badgeUseCase;

    /**
     * 모든 배지 목록 조회
     *
     * @return 전체 배지 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RankingBadgeResponse>>> getAllBadges() {
        try {
            log.info("전체 배지 목록 조회 요청");
            List<RankingBadgeResponse> badges = badgeUseCase.getAllBadges();

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "배지 목록을 성공적으로 조회했습니다.", badges)
            );
        } catch (Exception e) {
            log.error("배지 목록 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "배지 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자가 획득한 배지 목록 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자의 배지 목록
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<RankingBadgeResponse>>> getUserBadges(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 배지 목록 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 배지 목록 조회 요청: userId={}", userPrincipal.getId());
            List<RankingBadgeResponse> badges = badgeUseCase.getUserBadges(userPrincipal.getId());

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "배지 목록을 성공적으로 조회했습니다.", badges)
            );
        } catch (Exception e) {
            log.error("배지 목록 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "배지 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 특정 배지 상세 정보 조회
     *
     * @param badgeId 배지 ID
     * @return 배지 상세 정보
     */
    @GetMapping("/{badgeId}")
    public ResponseEntity<ApiResponse<RankingBadgeResponse>> getBadgeDetail(
        @PathVariable Long badgeId
    ) {
        try {
            log.info("배지 상세 정보 조회 요청: badgeId={}", badgeId);
            RankingBadgeResponse badge = badgeUseCase.getBadgeDetail(badgeId);

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "배지 상세 정보를 성공적으로 조회했습니다.", badge)
            );
        } catch (IllegalArgumentException e) {
            log.warn("배지 상세 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("배지 상세 정보 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "배지 상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 최근 획득한 배지 목록 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param limit         조회할 배지 수량 (기본값: 5)
     * @return 최근 획득한 배지 목록
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<RankingBadgeResponse>>> getRecentBadges(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam(value = "limit", defaultValue = "5") int limit
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 최근 획득 배지 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("최근 획득 배지 조회 요청: userId={}, limit={}", userPrincipal.getId(), limit);
            List<RankingBadgeResponse> badges = badgeUseCase.getRecentBadges(userPrincipal.getId(), limit);

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "최근 획득 배지를 성공적으로 조회했습니다.", badges)
            );
        } catch (Exception e) {
            log.error("최근 획득 배지 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "최근 획득 배지 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 특정 유형의 배지 목록 조회
     *
     * @param badgeType 배지 유형
     * @return 특정 유형의 배지 목록
     */
    @GetMapping("/type/{badgeType}")
    public ResponseEntity<ApiResponse<List<RankingBadgeResponse>>> getBadgesByType(
        @PathVariable String badgeType
    ) {
        try {
            log.info("배지 유형별 조회 요청: badgeType={}", badgeType);
            List<RankingBadgeResponse> badges = badgeUseCase.getBadgesByType(badgeType);

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "배지 목록을 성공적으로 조회했습니다.", badges)
            );
        } catch (Exception e) {
            log.error("배지 유형별 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "배지 유형별 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }
} 