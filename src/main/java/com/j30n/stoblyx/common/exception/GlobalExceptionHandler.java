package com.j30n.stoblyx.common.exception;

import com.j30n.stoblyx.adapter.in.web.exception.BusinessException;
import com.j30n.stoblyx.adapter.in.web.exception.TokenValidationException;
import com.j30n.stoblyx.adapter.in.web.exception.UnauthorizedException;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인자 예외 발생: {}", ex.getMessage());
        return errorResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("유효성 검사 예외 발생: {}", errorMessage);
        return errorResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("파라미터 '%s'의 타입이 올바르지 않습니다. 필요한 타입: %s", 
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "알 수 없음");
        log.warn("파라미터 타입 불일치 예외 발생: {}", errorMessage);
        return errorResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String errorMessage = String.format("필수 파라미터 '%s'가 누락되었습니다.", ex.getParameterName());
        log.warn("필수 파라미터 누락 예외 발생: {}", errorMessage);
        return errorResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("엔티티 찾을 수 없음 예외 발생: {}", ex.getMessage());
        return errorResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        log.warn("비즈니스 예외 발생: {}, 에러 코드: {}", ex.getMessage(), ex.getErrorCode());
        return errorResponseEntity(ex.getMessage(), ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleTokenValidationException(TokenValidationException ex) {
        log.warn("토큰 검증 예외 발생: {}", ex.getMessage());
        return errorResponseEntity(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("인증되지 않은 예외 발생: {}", ex.getMessage());
        return errorResponseEntity(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("접근 거부 예외 발생: {}", ex.getMessage());
        return errorResponseEntity("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationCredentialsNotFoundException(
            org.springframework.security.authentication.AuthenticationCredentialsNotFoundException ex) {
        log.warn("인증 정보 없음 예외 발생: {}", ex.getMessage());
        return errorResponseEntity("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("잘못된 인증 정보 예외 발생: {}", ex.getMessage());
        return errorResponseEntity("아이디 또는 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("인증 예외 발생: {}", ex.getMessage());
        return errorResponseEntity("인증이 필요합니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiResponse<?>> handleRedisConnectionFailureException(RedisConnectionFailureException ex) {
        log.error("Redis 연결 실패 예외 발생: {}", ex.getMessage(), ex);
        return errorResponseEntity("서비스 일시적 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDataAccessException(DataAccessException ex) {
        log.error("데이터 접근 예외 발생: {}", ex.getMessage(), ex);
        return errorResponseEntity("데이터 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException ex) {
        log.error("Null 포인터 예외 발생: {}", ex.getMessage(), ex);
        return errorResponseEntity("요청 처리 중 오류가 발생했습니다. (Null 참조)", HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<?>> handleSecurityException(SecurityException ex) {
        log.error("보안 예외 발생: {}", ex.getMessage(), ex);
        return errorResponseEntity("보안 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.error("요청 본문 처리 예외 발생: {}", ex.getMessage(), ex);
        return errorResponseEntity("요청 본문을 처리할 수 없습니다. 유효한 JSON 형식인지 확인하세요.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(
            org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        log.warn("요청한 리소스를 찾을 수 없음: {}", ex.getMessage());
        return errorResponseEntity("요청한 API 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        log.warn("지원하지 않는 HTTP 메서드: {}, 지원 메서드: {}", ex.getMethod(), ex.getSupportedMethods());
        return errorResponseEntity(
            String.format("해당 API는 %s 메서드를 지원하지 않습니다. 지원 메서드: %s", 
                ex.getMethod(), String.join(", ", ex.getSupportedHttpMethods().stream().map(m -> m.name()).toList())),
            HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllUncaughtException(Exception ex) {
        String errorClassSimpleName = ex.getClass().getSimpleName();
        log.error("처리되지 않은 예외 발생 ({}): {}", errorClassSimpleName, ex.getMessage(), ex);
        
        // 명확한 오류 메시지를 생성하기 위한 노력
        String errorMessage = "서버 내부 오류가 발생했습니다.";
        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            errorMessage = errorMessage + " (" + errorClassSimpleName + ")";
        }
        
        return errorResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ApiResponse<?>> errorResponseEntity(String message, HttpStatus status) {
        ApiResponse<?> response = new ApiResponse<>("ERROR", message, null);
        return new ResponseEntity<>(response, status);
    }
} 