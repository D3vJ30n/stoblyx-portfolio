package com.j30n.stoblyx.domain.model;

public enum ContentStatus {
    PROCESSING,    // AI가 콘텐츠 생성 중
    COMPLETED,     // 생성 완료
    FAILED,        // 생성 실패
    PUBLISHED,     // 공개됨
    PRIVATE,       // 비공개
    DELETED        // 삭제됨
} 