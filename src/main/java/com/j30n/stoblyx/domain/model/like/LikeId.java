package com.j30n.stoblyx.domain.model.like;

import lombok.Value;

/**
 * 좋아요 식별자 값 객체
 */
@Value
public class LikeId {
    Long value;

    public LikeId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("좋아요 ID는 양수여야 합니다");
        }
        this.value = value;
    }
} 