package com.j30n.stoblyx.adapter.web.controller;

import com.j30n.stoblyx.adapter.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.web.dto.comment.CommentUpdateRequest;
import com.j30n.stoblyx.application.service.comment.CommentService;
import com.j30n.stoblyx.common.annotation.CurrentUser;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long quoteId,
        @Valid @RequestBody CommentCreateRequest request
    ) {
        try {
            CommentResponse response = commentService.createComment(currentUser.getId(), quoteId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("SUCCESS", "댓글이 성공적으로 등록되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> getComment(
        @PathVariable Long id
    ) {
        try {
            CommentResponse response = commentService.getComment(id);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "댓글을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByQuote(
        @PathVariable Long quoteId,
        Pageable pageable
    ) {
        try {
            Page<CommentResponse> response = commentService.getCommentsByQuote(quoteId, pageable);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "댓글 목록을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByUser(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        try {
            Page<CommentResponse> response = commentService.getCommentsByUser(userId, pageable);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "댓글 목록을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id,
        @Valid @RequestBody CommentUpdateRequest request
    ) {
        try {
            CommentResponse response = commentService.updateComment(currentUser.getId(), id, request);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "댓글이 성공적으로 수정되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id
    ) {
        try {
            commentService.deleteComment(currentUser.getId(), id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 