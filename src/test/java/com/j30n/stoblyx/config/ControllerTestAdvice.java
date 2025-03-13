package com.j30n.stoblyx.config;

import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@TestConfiguration
@ControllerAdvice
@Primary
public class ControllerTestAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>("ERROR", fieldError.getDefaultMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>("ERROR", e.getMessage(), null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>("ERROR", e.getMessage(), null));
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>("ERROR", "요청한 리소스를 찾을 수 없습니다: " + e.getResourcePath(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse<>("ERROR", "접근 권한이 없습니다.", null));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>("ERROR", "아이디 또는 비밀번호가 잘못되었습니다.", null));
    }
    
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiResponse<>("ERROR", "인증이 필요한 요청입니다.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>("ERROR", "서버 내부 오류가 발생했습니다.", null));
    }
} 