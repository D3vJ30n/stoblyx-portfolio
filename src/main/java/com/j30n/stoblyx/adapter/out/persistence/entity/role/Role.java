package com.j30n.stoblyx.adapter.out.persistence.entity.role;

import org.hibernate.annotations.Comment;

/**
 * 사용자 권한을 정의하는 열거형
 */
public enum Role {
    /**
     * 일반 사용자 권한
     * 기본적인 서비스 이용이 가능합니다.
     */
    @Comment("일반 사용자")
    USER,

    /**
     * 관리자 권한
     * 시스템 관리 및 모든 기능에 대한 접근이 가능합니다.
     */
    @Comment("관리자")
    ADMIN
} 