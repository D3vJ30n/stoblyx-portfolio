package com.j30n.stoblyx.common.dto;

/**
 * API 응답을 위한 공통 포맷
 * @param status 응답 상태 ("success" 또는 "error")
 * @param message 응답 메시지
 * @param data 응답 데이터
 * @param <T> 응답 데이터의 타입
 */
public record ApiResponse<T>(String status, String message, T data) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null);
    }
} 