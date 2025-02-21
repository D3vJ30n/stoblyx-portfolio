package com.j30n.stoblyx.common.dto;

import lombok.Getter;

/**
 * API 응답을 위한 공통 포맷
 * 
 * @param <T> 응답 데이터의 타입
 */
@Getter
public class ApiResponse<T> {
    
    /**
     * API 응답 상태
     */
    public enum Status {
        SUCCESS, ERROR
    }

    private final Status status;
    private final String message;
    private final T data;
    private final int code;

    private ApiResponse(Status status, String message, T data, int code) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    /**
     * 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @param <T> 응답 데이터의 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(Status.SUCCESS, message, data, 200);
    }

    /**
     * 성공 응답을 생성합니다. (상태 코드 지정)
     *
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @param code HTTP 상태 코드
     * @param <T> 응답 데이터의 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message, T data, int code) {
        return new ApiResponse<>(Status.SUCCESS, message, data, code);
    }

    /**
     * 에러 응답을 생성합니다.
     *
     * @param message 에러 메시지
     * @param <T> 응답 데이터의 타입
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(Status.ERROR, message, null, 400);
    }

    /**
     * 에러 응답을 생성합니다. (상태 코드 지정)
     *
     * @param message 에러 메시지
     * @param code HTTP 상태 코드
     * @param <T> 응답 데이터의 타입
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(Status.ERROR, message, null, code);
    }

    /**
     * 현재 응답이 성공인지 확인합니다.
     *
     * @return 성공 여부
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /**
     * 현재 응답이 에러인지 확인합니다.
     *
     * @return 에러 여부
     */
    public boolean isError() {
        return status == Status.ERROR;
    }
}