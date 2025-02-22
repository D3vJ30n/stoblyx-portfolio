package com.j30n.stoblyx.domain.model.comment;

import lombok.Value;

/**
 * 댓글 식별자 값 객체
 */
@Value
public class CommentId {
    Long value;

    public CommentId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("댓글 ID는 양수여야 합니다");
        }
        this.value = value;
    }
}