package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentResponse;
import com.j30n.stoblyx.adapter.in.web.dto.comment.CommentUpdateRequest;
import com.j30n.stoblyx.application.service.comment.CommentService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 관련 API를 처리하는 컨트롤러
 * 댓글의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * 모든 엔드포인트는 API 버전 v1을 사용합니다.
 * 인증된 사용자만 댓글 작성, 수정, 삭제가 가능합니다.
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 특정 문구에 새로운 댓글을 작성합니다.
     * 인증된 사용자만 댓글을 작성할 수 있습니다.
     *
     * @param quoteId 댓글을 작성할 문구의 ID
     * @param request 댓글 생성 요청 DTO
     * @param currentUser 현재 인증된 사용자
     * @return 생성된 댓글 정보
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @PostMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
        @PathVariable Long quoteId,
        @Valid @RequestBody CommentCreateRequest request,
        @CurrentUser UserPrincipal currentUser
    ) {
        CommentResponse response = commentService.createComment(quoteId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("SUCCESS", "댓글이 성공적으로 등록되었습니다.", response));
    }

    /**
     * ID로 댓글을 조회합니다.
     *
     * @param id 조회할 댓글의 ID
     * @return 조회된 댓글 정보
     * @throws IllegalArgumentException 댓글이 존재하지 않는 경우
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> getComment(
        @PathVariable Long id
    ) {
        CommentResponse response = commentService.getComment(id);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "댓글을 성공적으로 조회했습니다.", response));
    }

    /**
     * 특정 문구에 작성된 댓글 목록을 페이징하여 조회합니다.
     *
     * @param quoteId 댓글을 조회할 문구의 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     * @throws IllegalArgumentException 문구가 존재하지 않는 경우
     */
    @GetMapping("/quotes/{quoteId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByQuote(
        @PathVariable Long quoteId,
        Pageable pageable
    ) {
        Page<CommentResponse> response = commentService.getCommentsByQuote(quoteId, pageable);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "댓글 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 특정 사용자가 작성한 댓글 목록을 페이징하여 조회합니다.
     *
     * @param userId 댓글을 조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getCommentsByUser(
        @PathVariable Long userId,
        Pageable pageable
    ) {
        Page<CommentResponse> response = commentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "댓글 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 특정 댓글을 수정합니다.
     * 인증된 사용자만 자신의 댓글을 수정할 수 있습니다.
     *
     * @param commentId 수정할 댓글의 ID
     * @param request 댓글 수정 요청 DTO
     * @param currentUser 현재 인증된 사용자
     * @return 수정된 댓글 정보
     * @throws IllegalArgumentException 댓글이 존재하지 않는 경우 또는 수정 권한이 없는 경우
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
        @PathVariable Long commentId,
        @Valid @RequestBody CommentUpdateRequest request,
        @CurrentUser UserPrincipal currentUser
    ) {
        CommentResponse response = commentService.updateComment(commentId, request, currentUser.getId());
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "댓글이 성공적으로 수정되었습니다.", response));
    }

    /**
     * 댓글을 삭제합니다.
     * 댓글 작성자만 삭제할 수 있습니다.
     *
     * @param currentUser 현재 인증된 사용자
     * @param id 삭제할 댓글의 ID
     * @throws IllegalArgumentException 댓글이 존재하지 않거나 삭제 권한이 없는 경우
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id
    ) {
        commentService.deleteComment(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * IllegalArgumentException 예외를 처리합니다.
     * 주로 엔티티를 찾을 수 없거나 권한이 없는 경우 발생합니다.
     *
     * @param e 발생한 IllegalArgumentException 예외
     * @return 에러 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>("ERROR", e.getMessage(), null));
    }
}