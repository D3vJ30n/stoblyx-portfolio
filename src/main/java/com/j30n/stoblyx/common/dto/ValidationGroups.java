package com.j30n.stoblyx.common.dto;

/**
 * Jakarta Validation을 위한 Validation Groups
 * 각 도메인의 유효성 검사 그룹을 정의합니다.
 */
public final class ValidationGroups {
    private ValidationGroups() {}

    /**
     * 생성 작업 시 사용할 validation group
     */
    public interface Create {}

    /**
     * 수정 작업 시 사용할 validation group
     */
    public interface Update {}

    /**
     * 삭제 작업 시 사용할 validation group
     */
    public interface Delete {}

    /**
     * 조회 작업 시 사용할 validation group
     */
    public interface Read {}
} 