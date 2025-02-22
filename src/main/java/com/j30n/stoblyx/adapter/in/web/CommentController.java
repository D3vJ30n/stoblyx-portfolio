package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.application.dto.comment.CommentDto;
import com.j30n.stoblyx.application.usecase.user.port.FindUserUseCase;
import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.domain.model.comment.Comment;
import com.j30n.stoblyx.domain.port.in.comment.CreateCommentUseCase;
import com.j30n.stoblyx.domain.port.in.comment.DeleteCommentUseCase;
import com.j30n.stoblyx.domain.port.in.comment.FindCommentUseCase;
import com.j30n.stoblyx.domain.port.in.comment.UpdateCommentUseCase;
import com.j30n.stoblyx.domain.port.in.quote.FindQuoteUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CreateCommentUseCase createCommentUseCase;
    private final UpdateCommentUseCase updateCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final FindCommentUseCase findCommentUseCase;
    private final FindQuoteUseCase findQuoteUseCase;
    private final FindUserUseCase findUserUseCase;

    /**
     * 인용구에 새로운 댓글을 작성합니다.
     *
     * @param command 댓글 작성 요청 정보
     * @return 생성된 댓글 정보
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentDto.Responses.CommentDetail>> createComment(
        @Valid @RequestBody CommentDto.Commands.Create command
    ) {
        try {
            var response = createCommentUseCase.createComment(command);
            return ResponseEntity.ok(ApiResponse.success("댓글이 작성되었습니다", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 댓글에 답글을 작성합니다.
     *
     * @param parentId 부모 댓글 ID
     * @param command  답글 작성 요청 정보
     * @return 생성된 답글 정보
     */
    @PostMapping("/{parentId}/replies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentDto.Responses.CommentDetail>> createReply(
        @PathVariable Long parentId,
        @Valid @RequestBody CommentDto.Commands.CreateReply command
    ) {
        try {
            var response = createCommentUseCase.createReply(command);
            return ResponseEntity.ok(ApiResponse.success("답글이 작성되었습니다", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 댓글 내용을 수정합니다.
     *
     * @param id      수정할 댓글 ID
     * @param command 댓글 수정 요청 정보
     * @return 수정된 댓글 정보
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentDto.Responses.CommentDetail>> updateComment(
        @PathVariable Long id,
        @Valid @RequestBody CommentDto.Commands.Update command
    ) {
        try {
            var response = updateCommentUseCase.updateContent(command);
            return ResponseEntity.ok(ApiResponse.success("댓글이 수정되었습니다", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param command 삭제할 댓글 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
        @PathVariable Long id,
        @Valid @RequestBody CommentDto.Commands.Delete command
    ) {
        try {
            deleteCommentUseCase.deleteComment(command);
            return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구에 달린 댓글 목록을 조회합니다.
     *
     * @param quoteId 인용구 ID
     * @return 댓글 목록
     */
    @GetMapping("/quote/{quoteId}")
    public ResponseEntity<ApiResponse<List<CommentDto.Responses.CommentDetail>>> getCommentsByQuote(
        @PathVariable Long quoteId
    ) {
        try {
            var query = new CommentDto.Queries.FindByQuote(quoteId);
            var response = findCommentUseCase.findByQuote(query);
            return ResponseEntity.ok(ApiResponse.success("댓글 목록을 조회했습니다", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자가 작성한 댓글 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 댓글 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CommentDto.Responses.CommentDetail>>> getCommentsByUser(
        @PathVariable Long userId
    ) {
        try {
            var query = new CommentDto.Queries.FindByUser(userId);
            var response = findCommentUseCase.findByUser(query);
            return ResponseEntity.ok(ApiResponse.success("댓글 목록을 조회했습니다", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Request/Response Records
    record CreateCommentRequest(
        @NotNull(message = "인용구 ID는 필수입니다")
        Long quoteId,

        @NotNull(message = "책 ID는 필수입니다")
        Long bookId,

        @NotBlank(message = "댓글 내용은 필수입니다")
        @Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다")
        String content
    ) {
    }

    record CreateReplyRequest(
        @NotBlank(message = "답글 내용은 필수입니다")
        @Size(max = 1000, message = "답글 내용은 1000자를 초과할 수 없습니다")
        String content
    ) {
    }

    record UpdateCommentRequest(
        @NotBlank(message = "댓글 내용은 필수입니다")
        @Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다")
        String content
    ) {
    }

    record CommentResponse(
        Long id,
        String content,
        Long userId,
        String userName,
        Long quoteId,
        Long bookId,
        boolean isDeleted,
        String createdAt,
        String modifiedAt
    ) {
        static CommentResponse from(Comment comment) {
            return new CommentResponse(
                comment.getId().getValue(),
                comment.getContent().getValue(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getQuote().getId().getValue(),
                comment.getBookId().value(),
                comment.isDeleted(),
                comment.getCreatedAt().toString(),
                comment.getModifiedAt().toString()
            );
        }
    }
} 