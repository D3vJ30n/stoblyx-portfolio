package com.j30n.stoblyx.common.dto;

/**
 * 도메인 Query를 위한 마커 인터페이스
 * 모든 Query DTO는 이 인터페이스를 구현해야 합니다.
 */
public interface Query {
    /**
     * Query의 유효성을 검증합니다.
     * 기본적으로 Jakarta Validation을 사용하지만,
     * 추가적인 검증이 필요한 경우 이 메서드를 구현합니다.
     *
     * @return 유효성 검증 결과
     */
    default boolean validate() {
        return true;
    }
} 