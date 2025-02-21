package com.j30n.stoblyx.domain.base;

import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 모든 도메인 엔티티의 기본이 되는 엔티티
 * 생성 시간과 수정 시간을 자동으로 관리합니다.
 */
@Getter
public abstract class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected void updateModifiedAt() {
        this.updatedAt = LocalDateTime.now();
    }
} 