package com.j30n.stoblyx.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 응답 공통 클래스
 *
 * @param <T> 응답 데이터 타입
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String result;    // SUCCESS or ERROR
    private String message;   // success or error message
    private T data;
} 