package com.j30n.stoblyx.adapter.web.controller;

import com.j30n.stoblyx.application.service.like.LikeService;
import com.j30n.stoblyx.common.annotation.CurrentUser;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<Boolean>> likeQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            likeService.likeQuote(currentUser.getId(), quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "문구 좋아요가 완료되었습니다.", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), false));
        }
    }

    @DeleteMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<Boolean>> unlikeQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            likeService.unlikeQuote(currentUser.getId(), quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "문구 좋아요가 취소되었습니다.", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/quotes/{quoteId}/status")
    public ResponseEntity<ApiResponse<Boolean>> hasLiked(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId
    ) {
        try {
            boolean hasLiked = likeService.hasLiked(currentUser.getId(), quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "문구 좋아요 상태를 조회했습니다.", hasLiked));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/quotes/{quoteId}/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
        @PathVariable Long quoteId
    ) {
        try {
            long count = likeService.getLikeCount(quoteId);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "문구 좋아요 수를 조회했습니다.", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/quotes")
    public ResponseEntity<ApiResponse<List<Long>>> getCurrentUserLikedQuoteIds(
        @CurrentUser UserPrincipal currentUser
    ) {
        try {
            List<Long> quoteIds = likeService.getLikedQuoteIds(currentUser.getId());
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "좋아요한 문구 목록을 조회했습니다.", quoteIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/users/{userId}/quotes")
    public ResponseEntity<ApiResponse<Page<Long>>> getLikedQuoteIds(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        try {
            Page<Long> quoteIds = likeService.getLikedQuoteIds(userId, pageable);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "좋아요한 문구 목록을 조회했습니다.", quoteIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}