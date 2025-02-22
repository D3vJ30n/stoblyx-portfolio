package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.domain.model.like.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 좋아요 관련 예외를 처리하는 핸들러
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LikeExceptionHandler {

    /**
     * 좋아요를 찾을 수 없을 때의 예외 처리
     */
    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleLikeNotFoundException(LikeNotFoundException e) {
        log.error("좋아요를 찾을 수 없음: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 좋아요 권한이 없을 때의 예외 처리
     */
    @ExceptionHandler(LikeNotAuthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLikeNotAuthorizedException(
        LikeNotAuthorizedException e
    ) {
        log.error("좋아요 권한 없음: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 이미 존재하는 좋아요를 생성하려 할 때의 예외 처리
     */
    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleLikeAlreadyExistsException(
        LikeAlreadyExistsException e
    ) {
        log.error("좋아요 중복 생성 시도: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 기타 좋아요 관련 예외 처리
     */
    @ExceptionHandler(LikeException.class)
    public ResponseEntity<ApiResponse<Void>> handleLikeException(LikeException e) {
        log.error("좋아요 처리 중 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 좋아요 유효성 검사 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("좋아요 유효성 검사 오류: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage()));
    }
} 