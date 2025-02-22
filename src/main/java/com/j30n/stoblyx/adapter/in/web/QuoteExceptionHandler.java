package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.domain.model.quote.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 인용구 관련 예외를 처리하는 핸들러
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class QuoteExceptionHandler {

    /**
     * 인용구를 찾을 수 없을 때의 예외 처리
     */
    @ExceptionHandler(QuoteNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuoteNotFoundException(QuoteNotFoundException e) {
        log.error("인용구를 찾을 수 없음: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 인용구 권한이 없을 때의 예외 처리
     */
    @ExceptionHandler(QuoteNotAuthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuoteNotAuthorizedException(
        QuoteNotAuthorizedException e
    ) {
        log.error("인용구 권한 없음: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 이미 존재하는 인용구를 생성하려 할 때의 예외 처리
     */
    @ExceptionHandler(QuoteAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuoteAlreadyExistsException(
        QuoteAlreadyExistsException e
    ) {
        log.error("인용구 중복 생성 시도: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 삭제된 인용구에 대한 작업 시도 시의 예외 처리
     */
    @ExceptionHandler(QuoteDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuoteDeletedException(QuoteDeletedException e) {
        log.error("삭제된 인용구에 대한 작업 시도: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 기타 인용구 관련 예외 처리
     */
    @ExceptionHandler(QuoteException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuoteException(QuoteException e) {
        log.error("인용구 처리 중 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 인용구 유효성 검사 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("인용구 유효성 검사 오류: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage()));
    }
} 