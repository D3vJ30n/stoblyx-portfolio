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
} 