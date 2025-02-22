package com.j30n.stoblyx.common.dto;

import java.time.LocalDateTime;

/**
 * 도메인 Response를 위한 마커 인터페이스
 * 모든 Response DTO는 이 인터페이스를 구현해야 합니다.
 */
public interface Response {
    /**
     * 응답의 상태를 반환합니다.
     * 
     * @return 응답 상태 (SUCCESS, ERROR 등)
     */
    String getStatus();

    /**
     * 응답 생성 시간을 반환합니다.
     * 
     * @return 응답 생성 시간
     */
    LocalDateTime getTimestamp();
} 