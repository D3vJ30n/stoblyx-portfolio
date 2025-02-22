package com.j30n.stoblyx.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 응답을 위한 공통 응답 객체
 * @param <T> 응답 데이터의 타입
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private String result;    // SUCCESS or ERROR
    private String message;   // 응답 메시지
    private T data;          // 응답 데이터
    private LocalDateTime timestamp; // 응답 시간

    /**
     * 성공 응답을 생성합니다.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data, LocalDateTime.now());
    }

    /**
     * 성공 응답을 생성합니다. (데이터 없음)
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>("SUCCESS", message, null, LocalDateTime.now());
    }

    /**
     * 에러 응답을 생성합니다.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null, LocalDateTime.now());
    }

    /**
     * 에러 응답을 생성합니다. (에러 데이터 포함)
     */
    public static <T> ApiResponse<T> error(String message, T errorData) {
        return new ApiResponse<>("ERROR", message, errorData, LocalDateTime.now());
    }
}