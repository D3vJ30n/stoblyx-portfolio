package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ApiResponse;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.content.ContentCommentUpdateRequest;
import com.j30n.stoblyx.application.port.in.content.ContentCommentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * 콘텐츠 댓글 컨트롤러
 */
@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentCommentController {

    private final ContentCommentUseCase contentCommentUseCase;
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String RESULT_ERROR = "ERROR";

    /**
     * 콘텐츠에 댓글 작성
     */
    @PostMapping("/{contentId}/comments")
    public ResponseEntity<ApiResponse<ContentCommentResponse>> createComment(
            @PathVariable Long contentId,
            @RequestBody @Valid ContentCommentCreateRequest request,
            @RequestAttribute("userId") Long userId) {
        try {
            ContentCommentResponse commentResponse = contentCommentUseCase.createComment(contentId, request, userId);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "댓글이 성공적으로 등록되었습니다.", commentResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<ContentCommentResponse>> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid ContentCommentUpdateRequest request,
            @RequestAttribute("userId") Long userId) {
        try {
            ContentCommentResponse commentResponse = contentCommentUseCase.updateComment(commentId, request, userId);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "댓글이 성공적으로 수정되었습니다.", commentResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestAttribute("userId") Long userId) {
        try {
            contentCommentUseCase.deleteComment(commentId, userId);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "댓글이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 콘텐츠의 최상위 댓글 목록 조회
     */
    @GetMapping("/{contentId}/comments")
    public ResponseEntity<ApiResponse<Page<ContentCommentResponse>>> getTopLevelComments(
            @PathVariable Long contentId,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<ContentCommentResponse> comments = contentCommentUseCase.getTopLevelCommentsByContent(contentId, pageable);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "댓글 목록을 성공적으로 조회했습니다.", comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 댓글의 답글 목록 조회
     */
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<ContentCommentResponse>>> getReplies(
            @PathVariable Long commentId) {
        try {
            List<ContentCommentResponse> replies = contentCommentUseCase.getRepliesByParentId(commentId);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "답글 목록을 성공적으로 조회했습니다.", replies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }

    /**
     * 사용자가 작성한 댓글 목록 조회
     */
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<ApiResponse<Page<ContentCommentResponse>>> getUserComments(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<ContentCommentResponse> comments = contentCommentUseCase.getCommentsByUser(userId, pageable);
            return ResponseEntity.ok(new ApiResponse<>(RESULT_SUCCESS, "사용자 댓글 목록을 성공적으로 조회했습니다.", comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(RESULT_ERROR, e.getMessage(), null));
        }
    }
} 