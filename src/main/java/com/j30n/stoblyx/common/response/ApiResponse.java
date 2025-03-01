package com.j30n.stoblyx.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 응답의 표준 형식을 정의하는 클래스
 * 모든 API 응답은 이 클래스를 통해 일관된 형식으로 반환됩니다.
 *
 * @param <T> 응답 데이터의 타입
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * API 처리 결과를 나타내는 상태값
     * SUCCESS: 정상 처리됨
     * ERROR: 오류 발생
     */
    private String result;    // SUCCESS or ERROR

    /**
     * API 처리 결과에 대한 상세 메시지
     * 성공 시: 처리 결과에 대한 설명
     * 실패 시: 오류에 대한 상세 설명
     */
    private String message;   // success or error message

    /**
     * API 응답 데이터
     * 성공 시: 요청한 데이터
     * 실패 시: null
     */
    private T data;

    /**
     * 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @return 성공 응답 객체
     * @param <T> 응답 데이터의 타입
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    /**
     * 데이터가 없는 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("SUCCESS", message, null);
    }

    /**
     * 오류 응답을 생성합니다.
     *
     * @param message 오류 메시지
     * @return 오류 응답 객체
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }
} 