package com.j30n.stoblyx.domain.enums;

/**
 * 숏폼 콘텐츠의 처리 상태를 나타내는 열거형
 */
public enum ContentStatus {
    /**
     * 콘텐츠가 처리 중인 상태
     */
    PROCESSING,
    
    /**
     * 콘텐츠 생성이 완료된 상태
     */
    COMPLETED,
    
    /**
     * 콘텐츠 생성이 실패한 상태
     */
    FAILED,
    
    /**
     * 콘텐츠가 출판된 상태 (사용자에게 공개됨)
     */
    PUBLISHED
} 