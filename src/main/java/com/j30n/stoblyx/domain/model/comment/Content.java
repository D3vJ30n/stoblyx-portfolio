package com.j30n.stoblyx.domain.model.comment;

import lombok.Value;

/**
 * 댓글 내용 값 객체
 */
@Value
public class Content {
    String value;

    public Content(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다");
        }
        if (value.length() > 1000) {
            throw new IllegalArgumentException("댓글 내용은 1000자를 초과할 수 없습니다");
        }
        this.value = value.trim();
    }
}