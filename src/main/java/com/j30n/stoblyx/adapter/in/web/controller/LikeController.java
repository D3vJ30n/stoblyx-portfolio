package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.application.service.like.LikeService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 좋아요 관련 API를 처리하는 컨트롤러
 * 문구에 대한 좋아요 생성, 취소, 상태 조회, 통계 조회 기능을 제공합니다.
 * 모든 엔드포인트는 API 버전 v1을 사용합니다.
 * 인증된 사용자만 좋아요 생성, 취소가 가능합니다.
 */
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";

    private final LikeService likeService;

    /**
     * 특정 문구에 좋아요를 표시합니다.
     * 이미 좋아요한 문구에 대해서는 중복 좋아요가 불가능합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 좋아요할 문구의 ID
     * @return 좋아요 처리 결과 (true: 성공, false: 실패)
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @PostMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<Boolean>> likeQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            likeService.likeQuote(currentUser.getId(), quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>(RESULT_SUCCESS, "문구 좋아요가 완료되었습니다.", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), false));
        }
    }

    /**
     * 특정 문구의 좋아요를 취소합니다.
     * 좋아요하지 않은 문구에 대해서는 취소가 불가능합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 좋아요를 취소할 문구의 ID
     * @return 좋아요 취소 처리 결과 (false: 성공적으로 취소됨)
     * @throws IllegalArgumentException 문구가 존재하지 않거나 좋아요하지 않은 경우
     */
    @DeleteMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<Boolean>> unlikeQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            likeService.unlikeQuote(currentUser.getId(), quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>(RESULT_SUCCESS, "문구 좋아요가 취소되었습니다.", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 현재 사용자가 특정 문구에 좋아요를 했는지 확인합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param quoteId 확인할 문구의 ID
     * @return 좋아요 상태 (true: 좋아요 함, false: 좋아요 안함)
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @GetMapping("/quotes/{quoteId}/status")
    public ResponseEntity<ApiResponse<Boolean>> quoteLiked(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            boolean hasLiked = likeService.isLiked(currentUser.getId(), quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>(RESULT_SUCCESS, "문구 좋아요 상태를 조회했습니다.", hasLiked));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 특정 문구의 전체 좋아요 수를 조회합니다.
     *
     * @param quoteId 조회할 문구의 ID
     * @return 해당 문구의 전체 좋아요 수
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @GetMapping("/quotes/{quoteId}/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
        @PathVariable Long quoteId
    ) {
        try {
            long count = likeService.getLikeCount(quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>(RESULT_SUCCESS, "문구 좋아요 수를 조회했습니다.", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 현재 사용자가 좋아요한 모든 문구의 ID 목록을 조회합니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @return 사용자가 좋아요한 문구 ID 목록
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     */
    @GetMapping("/quotes")
    public ResponseEntity<ApiResponse<List<Long>>> getCurrentUserLikedQuoteIds(
        @CurrentUser UserPrincipal currentUser
    ) {
        try {
            List<Long> quoteIds = likeService.getLikedQuoteIds(currentUser.getId());
            return ResponseEntity.ok()
                .body(new ApiResponse<>(RESULT_SUCCESS, "좋아요한 문구 목록을 조회했습니다.", quoteIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 특정 사용자가 좋아요한 문구 ID 목록을 페이징하여 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 페이징된 문구 ID 목록
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     */
    @GetMapping("/users/{userId}/quotes")
    public ResponseEntity<ApiResponse<Page<Long>>> getLikedQuoteIds(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        try {
            Page<Long> quoteIds = likeService.getLikedQuoteIds(userId, pageable);
            return ResponseEntity.ok()
                .body(new ApiResponse<>(RESULT_SUCCESS, "좋아요한 문구 목록을 조회했습니다.", quoteIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }
}