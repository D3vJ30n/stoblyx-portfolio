package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ranking.UserRankingResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestRequest;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserInterestResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserProfileResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.UserUpdateRequest;
import com.j30n.stoblyx.application.port.in.ranking.RankingUserScoreUseCase;
import com.j30n.stoblyx.application.port.in.user.UserInterestUseCase;
import com.j30n.stoblyx.application.port.in.user.UserUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";
    private static final String ERROR_USER_NOT_AUTHENTICATED = "인증된 사용자 정보를 찾을 수 없습니다.";

    private final UserUseCase userUseCase;
    private final UserInterestUseCase userInterestUseCase;
    private final RankingUserScoreUseCase rankingUserScoreUseCase;

    /**
     * 현재 사용자의 프로필을 조회합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자 프로필 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            log.info("현재 사용자 프로필 조회 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 프로필 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 프로필 조회: userId={}", userPrincipal.getId());
            UserProfileResponse profile = userUseCase.getCurrentUser(userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 프로필을 성공적으로 조회했습니다.", profile));
        } catch (Exception e) {
            log.error("사용자 프로필 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "프로필 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자 프로필을 수정합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param request       수정할 프로필 정보
     * @return 수정된 사용자 프로필 정보
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
        @CurrentUser UserPrincipal userPrincipal,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        try {
            log.info("사용자 프로필 수정 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 프로필 수정 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 프로필 수정: userId={}", userPrincipal.getId());
            UserProfileResponse updatedProfile = userUseCase.updateUser(userPrincipal.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 프로필을 성공적으로 수정했습니다.", updatedProfile));
        } catch (Exception e) {
            log.error("사용자 프로필 수정 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "프로필 수정 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자 계정을 삭제합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            log.info("사용자 계정 삭제 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 계정 삭제 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 계정 삭제: userId={}", userPrincipal.getId());
            userUseCase.deleteUser(userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 계정이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("사용자 계정 삭제 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "계정 삭제 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자의 관심사를 조회합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자 관심사 정보
     */
    @GetMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> getUserInterest(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            log.info("사용자 관심사 조회 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 관심사 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 관심사 조회: userId={}", userPrincipal.getId());
            UserInterestResponse interestResponse = userInterestUseCase.getUserInterest(userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 관심사를 성공적으로 조회했습니다.", interestResponse));
        } catch (Exception e) {
            log.error("사용자 관심사 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "관심사 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자의 관심사를 업데이트합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param request       수정할 관심사 정보
     * @return 업데이트된 관심사 정보
     */
    @PutMapping("/me/interests")
    public ResponseEntity<ApiResponse<UserInterestResponse>> updateUserInterest(
        @CurrentUser UserPrincipal userPrincipal,
        @Valid @RequestBody UserInterestRequest request
    ) {
        try {
            log.info("사용자 관심사 업데이트 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 관심사 업데이트 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 관심사 업데이트: userId={}", userPrincipal.getId());
            UserInterestResponse updatedInterest = userInterestUseCase.updateUserInterest(userPrincipal.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 관심사를 성공적으로 업데이트했습니다.", updatedInterest));
        } catch (Exception e) {
            log.error("사용자 관심사 업데이트 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "관심사 업데이트 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자 프로필 이미지를 업로드합니다.
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param image         업로드할 이미지 파일
     * @return 업데이트된 사용자 프로필 정보
     */
    @PostMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadProfileImage(
        @CurrentUser UserPrincipal userPrincipal,
        @RequestParam("image") MultipartFile image
    ) {
        try {
            log.info("사용자 프로필 이미지 업로드 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 프로필 이미지 업로드 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            if (image.isEmpty()) {
                log.warn("빈 이미지 파일 업로드 시도: userId={}", userPrincipal.getId());
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, "이미지 파일이 제공되지 않았습니다.", null)
                );
            }

            log.info("사용자 프로필 이미지 업로드: userId={}, fileSize={}", userPrincipal.getId(), image.getSize());
            UserProfileResponse updatedProfile = userUseCase.updateProfileImage(userPrincipal.getId(), image);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "프로필 이미지가 성공적으로 업로드되었습니다.", updatedProfile));
        } catch (Exception e) {
            log.error("사용자 프로필 이미지 업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "프로필 이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 사용자 랭킹 정보 조회
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @return 사용자 랭킹 정보
     */
    @GetMapping("/me/ranking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserRankingResponse>> getUserRanking(
        @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            log.info("사용자 랭킹 정보 조회 요청");
            if (userPrincipal == null) {
                log.warn("인증된 사용자 정보 없음 - 랭킹 정보 조회 실패");
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(RESULT_ERROR, ERROR_USER_NOT_AUTHENTICATED, null)
                );
            }

            log.info("사용자 랭킹 정보 조회: userId={}", userPrincipal.getId());
            // 사용자 점수 정보 조회
            RankingUserScore userScore = rankingUserScoreUseCase.getUserScore(userPrincipal.getId());

            // 전체 랭킹에서 사용자 순위 계산
            List<RankingUserScore> allUsers = rankingUserScoreUseCase.getTopUsers(Integer.MAX_VALUE);
            int rank = 0;
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUserId().equals(userPrincipal.getId())) {
                    rank = i + 1; // 0-based 인덱스를 1-based 순위로 변환
                    break;
                }
            }

            UserRankingResponse response = UserRankingResponse.fromEntity(userScore, rank);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 랭킹 정보를 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            log.error("사용자 랭킹 정보 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "랭킹 정보 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 특정 사용자의 프로필을 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
        @PathVariable Long userId
    ) {
        try {
            log.info("사용자 프로필 조회 요청: userId={}", userId);
            UserProfileResponse profile = userUseCase.getCurrentUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 프로필을 성공적으로 조회했습니다.", profile));
        } catch (Exception e) {
            log.error("사용자 프로필 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(RESULT_ERROR, "프로필 조회 중 오류가 발생했습니다: " + e.getMessage(), null)
            );
        }
    }

    /**
     * 유저 프로필 정보를 조회합니다.
     * K6 테스트를 위한 엔드포인트입니다.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getUserProfile(
        @RequestParam(required = false) Long userId,
        @RequestHeader(value = "Authorization", required = false) String token
    ) {
        try {
            // 테스트를 위한 샘플 유저 프로필 데이터
            Map<String, Object> userProfile = new HashMap<>();

            // 기본 유저 정보
            userProfile.put("id", userId != null ? userId : 1L);
            userProfile.put("username", "testuser");
            userProfile.put("nickname", "테스트 사용자");
            userProfile.put("email", "test@example.com");
            userProfile.put("profileImageUrl", "https://example.com/profile.jpg");
            userProfile.put("bio", "안녕하세요. 테스트 사용자입니다.");

            // 활동 통계
            userProfile.put("contentCount", 15);
            userProfile.put("followingCount", 42);
            userProfile.put("followerCount", 38);
            userProfile.put("likeCount", 156);

            // 랭킹 정보
            Map<String, Object> rankInfo = new HashMap<>();
            rankInfo.put("currentRank", "GOLD");
            rankInfo.put("points", 780);
            rankInfo.put("nextRank", "PLATINUM");
            rankInfo.put("pointsToNextRank", 220);
            userProfile.put("rankInfo", rankInfo);

            return ResponseEntity.ok(
                ApiResponse.success("유저 프로필 정보입니다.", userProfile)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("유저 프로필 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}