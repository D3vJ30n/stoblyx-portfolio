package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.reward.GamificationRewardClaimRequest;
import com.j30n.stoblyx.adapter.in.web.dto.reward.GamificationRewardResponse;
import com.j30n.stoblyx.application.port.in.gamification.GamificationRewardUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.model.GamificationReward;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 보상 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class GamificationRewardController {

    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";
    private static final String ERROR_USER_NOT_AUTHENTICATED = "인증된 사용자 정보를 찾을 수 없습니다.";

    private final GamificationRewardUseCase gamificationRewardUseCase;

    /**
     * 사용자의 모든 보상 내역 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 보상 내역 목록
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<GamificationRewardResponse>>> getUserRewards(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 보상 내역 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 보상 내역 조회 요청: userId={}", userPrincipal.getId());
            List<GamificationReward> rewards = gamificationRewardUseCase.getUserRewards(userPrincipal.getId());
            List<GamificationRewardResponse> gamificationRewardRespons = rewards.stream()
                .map(this::convertToRewardResponse)
                .toList();

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "보상 내역을 성공적으로 조회했습니다.", gamificationRewardRespons)
            );
        } catch (Exception e) {
            log.error("보상 내역 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "보상 내역 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용 가능한 보상 목록 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용 가능한 보상 목록
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<GamificationRewardResponse>>> getAvailableRewards(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 사용 가능한 보상 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용 가능한 보상 조회 요청: userId={}", userPrincipal.getId());
            List<GamificationReward> unclaimedRewards = gamificationRewardUseCase.getUnclaimedRewards().stream()
                .filter(reward -> reward.getUserId().equals(userPrincipal.getId()) && !reward.isExpired())
                .toList();

            List<GamificationRewardResponse> gamificationRewardRespons = unclaimedRewards.stream()
                .map(this::convertToRewardResponse)
                .toList();

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "사용 가능한 보상을 성공적으로 조회했습니다.", gamificationRewardRespons)
            );
        } catch (Exception e) {
            log.error("사용 가능한 보상 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "사용 가능한 보상 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 보상 수령
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param rewardId      보상 ID
     * @param request       보상 수령 요청 정보
     * @return 수령된 보상 정보
     */
    @PostMapping("/{rewardId}/claim")
    public ResponseEntity<ApiResponse<GamificationRewardResponse>> claimReward(
        @CurrentUser UserPrincipal userPrincipal,
        @PathVariable Long rewardId,
        @RequestBody(required = false) GamificationRewardClaimRequest request
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 보상 수령 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("보상 수령 요청: userId={}, rewardId={}", userPrincipal.getId(), rewardId);

            // 보상 정보 조회 (rewardId에 해당하는 보상이 사용자의 것인지 먼저 확인)
            // 사용자의 보상 내역을 확인하고 보상 ID가 해당 사용자의 것인지 검증
            List<GamificationReward> userRewards = gamificationRewardUseCase.getUserRewards(userPrincipal.getId());
            boolean hasAccess = userRewards.stream()
                .anyMatch(reward -> reward.getId().equals(rewardId));
            
            if (!hasAccess) {
                throw new IllegalArgumentException("접근 권한이 없는 보상입니다.");
            }

            // 보상 수령 처리
            GamificationReward reward = gamificationRewardUseCase.claimReward(rewardId);
            GamificationRewardResponse gamificationRewardResponse = convertToRewardResponse(reward);

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "보상을 성공적으로 수령했습니다.", gamificationRewardResponse)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("보상 수령 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("보상 수령 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "보상 수령 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 특정 보상 상세 정보 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param rewardId      보상 ID
     * @return 보상 상세 정보
     */
    @GetMapping("/{rewardId}")
    public ResponseEntity<ApiResponse<GamificationRewardResponse>> getRewardDetail(
        @CurrentUser UserPrincipal userPrincipal,
        @PathVariable Long rewardId
    ) {
        try {
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 보상 상세 정보 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("보상 상세 정보 조회 요청: userId={}, rewardId={}", userPrincipal.getId(), rewardId);

            // 사용자의 보상 내역을 확인하고 보상 ID가 해당 사용자의 것인지 검증
            List<GamificationReward> userRewards = gamificationRewardUseCase.getUserRewards(userPrincipal.getId());
            
            GamificationReward reward = userRewards.stream()
                .filter(r -> r.getId().equals(rewardId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 보상을 찾을 수 없거나 접근 권한이 없습니다."));

            GamificationRewardResponse gamificationRewardResponse = convertToRewardResponse(reward);

            return ResponseEntity.ok(
                new ApiResponse<>(RESULT_SUCCESS, "보상 상세 정보를 성공적으로 조회했습니다.", gamificationRewardResponse)
            );
        } catch (IllegalArgumentException e) {
            log.warn("보상 상세 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, e.getMessage(), null)
            );
        } catch (Exception e) {
            log.error("보상 상세 정보 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "보상 상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * GamificationReward를 RewardResponse로 변환
     *
     * @param reward GamificationReward 객체
     * @return RewardResponse 객체
     */
    private GamificationRewardResponse convertToRewardResponse(GamificationReward reward) {
        return new GamificationRewardResponse(
            reward.getId(),
            reward.getRewardType().name(),
            reward.getRewardType().getDisplayName(),
            reward.getDescription(),
            reward.getPoints(),
            reward.isClaimed(),
            null, // claimedAt 필드 없음
            reward.getExpiryDate(),
            null, // benefitCode 필드 없음
            null  // imageUrl 필드 없음
        );
    }
} 