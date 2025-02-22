package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.domain.model.comment.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 댓글 관련 예외를 처리하는 핸들러
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommentExceptionHandler {

    /**
     * 댓글을 찾을 수 없을 때의 예외 처리
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentNotFoundException(CommentNotFoundException e) {
        log.error("댓글을 찾을 수 없음: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 댓글 권한이 없을 때의 예외 처리
     */
    @ExceptionHandler(CommentNotAuthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentNotAuthorizedException(
        CommentNotAuthorizedException e
    ) {
        log.error("댓글 권한 없음: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 삭제된 댓글에 대한 작업 시도 시의 예외 처리
     */
    @ExceptionHandler(CommentDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentDeletedException(CommentDeletedException e) {
        log.error("삭제된 댓글에 대한 작업 시도: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 기타 댓글 관련 예외 처리
     */
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentException(CommentException e) {
        log.error("댓글 처리 중 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 댓글 유효성 검사 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("댓글 유효성 검사 오류: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage()));
    }
} 