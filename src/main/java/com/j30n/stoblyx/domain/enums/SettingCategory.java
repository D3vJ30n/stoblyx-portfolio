package com.j30n.stoblyx.domain.enums;

/**
 * 시스템 설정의 카테고리를 정의하는 열거형
 */
public enum SettingCategory {
    /**
     * API 키 관련 설정 (Pexels API 키 등)
     */
    API_KEY,
    
    /**
     * 리소스 경로 관련 설정 (미디어 파일 저장 경로 등)
     */
    RESOURCE_PATH,
    
    /**
     * 캐시 관련 설정 (Redis TTL 등)
     */
    CACHE,
    
    /**
     * 랭킹 시스템 관련 설정 (점수 계산 알고리즘 파라미터 등)
     */
    RANKING,
    
    /**
     * 게이미피케이션 관련 설정 (랭크별 혜택 및 조건 등)
     */
    GAMIFICATION,
    
    /**
     * 보안 관련 설정
     */
    SECURITY,
    
    /**
     * 기타 설정
     */
    OTHER
} 