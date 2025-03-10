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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
} 