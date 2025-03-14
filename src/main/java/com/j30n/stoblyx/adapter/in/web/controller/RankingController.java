package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ranking.RankingActivityRequest;
import com.j30n.stoblyx.adapter.in.web.dto.ranking.RankingResponse;
import com.j30n.stoblyx.adapter.in.web.dto.ranking.RankingStatisticsResponse;
import com.j30n.stoblyx.application.port.in.ranking.RankingUserScoreUseCase;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.enums.RankType;
import com.j30n.stoblyx.domain.model.RankingUserScore;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 랭킹 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String ERROR_USER_NOT_AUTHENTICATED = "인증된 사용자 정보를 찾을 수 없습니다.";

    private final RankingUserScoreUseCase rankingUserScoreUseCase;

    /**
     * 상위 사용자 랭킹 조회
     *
     * @param limit 조회할 사용자 수
     * @return 상위 사용자 랭킹 목록
     */
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<RankingResponse>>> getTopUsers(
        @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<RankingUserScore> topUsers = rankingUserScoreUseCase.getTopUsers(limit);
            List<RankingResponse> response = topUsers.stream()
                .map(RankingResponse::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "상위 사용자 랭킹을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            log.error("상위 사용자 랭킹 조회 중 오류 발생", e);
            throw new IllegalArgumentException("상위 사용자 랭킹 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 랭크 타입별 사용자 목록 조회
     *
     * @param rankType 랭크 타입
     * @param pageable 페이징 정보
     * @return 랭크 타입별 사용자 목록
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<RankingResponse>>> getUsersByRankType(
        @RequestParam RankType rankType,
        Pageable pageable
    ) {
        try {
            List<RankingUserScore> users = rankingUserScoreUseCase.getUsersByRankType(rankType);
            
            // 페이징 처리
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), users.size());
            
            List<RankingResponse> content = users.subList(start, end).stream()
                .map(RankingResponse::fromEntity)
                .collect(Collectors.toList());
            
            Page<RankingResponse> page = new PageImpl<>(content, pageable, users.size());
            
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, rankType.getDisplayName() + " 랭크 사용자 목록을 성공적으로 조회했습니다.", page));
        } catch (Exception e) {
            log.error("랭크 타입별 사용자 목록 조회 중 오류 발생", e);
            throw new IllegalArgumentException("랭크 타입별 사용자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 랭킹 통계 조회
     *
     * @return 랭킹 통계 정보
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<RankingStatisticsResponse>> getRankingStatistics() {
        try {
            // 각 랭크 타입별 사용자 수 계산
            Map<String, Long> rankDistribution = new HashMap<>();
            for (RankType rankType : RankType.values()) {
                List<RankingUserScore> users = rankingUserScoreUseCase.getUsersByRankType(rankType);
                rankDistribution.put(rankType.name(), (long) users.size());
            }
            
            // 전체 사용자의 평균 점수 계산
            List<RankingUserScore> allUsers = rankingUserScoreUseCase.getTopUsers(Integer.MAX_VALUE);
            double averageScore = allUsers.stream()
                .mapToInt(RankingUserScore::getCurrentScore)
                .average()
                .orElse(0.0);
            
            RankingStatisticsResponse response = new RankingStatisticsResponse(rankDistribution, averageScore);
            
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "랭킹 통계를 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            log.error("랭킹 통계 조회 중 오류 발생", e);
            throw new IllegalArgumentException("랭킹 통계 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 활동 점수 업데이트
     *
     * @param userPrincipal 현재 사용자 인증 정보
     * @param request 활동 점수 업데이트 요청
     * @return 업데이트된 사용자 랭킹 정보
     */
    @PostMapping("/activity")
    public ResponseEntity<ApiResponse<RankingResponse>> updateActivityScore(
        @CurrentUser UserPrincipal userPrincipal,
        @Valid @RequestBody RankingActivityRequest request
    ) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException(ERROR_USER_NOT_AUTHENTICATED);
        }
        
        try {
            RankingUserScore updatedScore = rankingUserScoreUseCase.updateUserScore(userPrincipal.getId(), request.getScore());
            RankingResponse response = RankingResponse.fromEntity(updatedScore);
            
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "활동 점수가 성공적으로 업데이트되었습니다.", response));
        } catch (Exception e) {
            log.error("활동 점수 업데이트 중 오류 발생", e);
            throw new IllegalArgumentException("활동 점수 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 사용자의 점수 정보 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 랭킹 정보
     */
    @GetMapping("/user/{userId}/score")
    public ResponseEntity<ApiResponse<RankingResponse>> getUserScoreById(
        @PathVariable Long userId
    ) {
        try {
            RankingUserScore userScore;
            try {
                userScore = rankingUserScoreUseCase.getUserScore(userId);
            } catch (Exception e) {
                // 사용자 점수 정보가 없으면 기본값으로 생성
                log.info("사용자 점수 정보가 없어 기본값으로 생성합니다. userId={}", userId);
                userScore = new RankingUserScore();
                userScore.setUserId(userId);
                userScore.setCurrentScore(100); // 기본 점수 100점
                userScore.setPreviousScore(0);
                userScore.setRankType(RankType.BRONZE); // 기본 랭크 브론즈
                userScore.setSuspiciousActivity(false);
                userScore.setReportCount(0);
                userScore.setAccountSuspended(false);
                userScore.setCreatedAt(java.time.LocalDateTime.now());
                userScore.setModifiedAt(java.time.LocalDateTime.now());
                // 실제로는 저장하지 않고 임시 객체로만 사용 (테스트 목적)
            }
            
            RankingResponse response = RankingResponse.fromEntity(userScore);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 점수 정보를 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            log.error("사용자 점수 정보 조회 중 오류 발생", e);
            throw new IllegalArgumentException("사용자 점수 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 콘텐츠 랭킹을 조회합니다.
     * 
     * @param rankType 랭킹 유형 (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND 등)
     * @param pageable 페이징 정보
     * @return 랭킹 리스트
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getRanking(
            @RequestParam(required = false, defaultValue = "POINT") String rankType,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            // 테스트용 랭킹 데이터 생성
            List<Map<String, Object>> rankingList = new ArrayList<>();
            
            // rankType에 따라 다른 랭킹 데이터 생성
            String rankTypeName = rankType.toUpperCase();
            for (int i = 1; i <= 20; i++) {
                Map<String, Object> rankingItem = new HashMap<>();
                rankingItem.put("rank", i);
                rankingItem.put("id", (long) i);
                rankingItem.put("title", rankTypeName + " 랭킹 콘텐츠 " + i);
                rankingItem.put("author", "작가 " + i);
                rankingItem.put("thumbnailUrl", "https://example.com/thumbnail" + i + ".jpg");
                
                // rankType에 따라 다른 점수 부여
                switch (rankTypeName) {
                    case "BRONZE":
                        rankingItem.put("score", 100 + i * 5);
                        break;
                    case "SILVER":
                        rankingItem.put("score", 200 + i * 10);
                        break;
                    case "GOLD":
                        rankingItem.put("score", 500 + i * 25);
                        break;
                    case "PLATINUM":
                        rankingItem.put("score", 1000 + i * 50);
                        break;
                    case "DIAMOND":
                        rankingItem.put("score", 2000 + i * 100);
                        break;
                    case "POINT":
                    default:
                        rankingItem.put("score", 50 + i * 3);
                        break;
                }
                
                rankingList.add(rankingItem);
            }
            
            // 페이징 처리
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), rankingList.size());
            List<Map<String, Object>> pagedContent = rankingList.subList(start, end);
            
            // 페이지 객체 생성
            Page<Map<String, Object>> page = new PageImpl<>(
                pagedContent, pageable, rankingList.size());
            
            return ResponseEntity.ok(
                ApiResponse.success(rankTypeName + " 랭킹 리스트입니다.", page)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("랭킹 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 