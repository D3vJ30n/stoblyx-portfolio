package com.j30n.stoblyx.adapter.in.web.dto.video;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VideoCreateRequest(
    @NotNull(message = "인용구 ID는 필수입니다")
    Long quoteId,

    @NotEmpty(message = "비디오 스타일은 필수입니다")
    String style,

    @NotEmpty(message = "BGM 타입은 필수입니다")
    String bgmType
) {
    public VideoCreateRequest {
        if (quoteId == null) {
            throw new IllegalArgumentException("인용구 ID는 필수입니다");
        }
        if (style == null || style.trim().isEmpty()) {
            throw new IllegalArgumentException("비디오 스타일은 필수입니다");
        }
        if (bgmType == null || bgmType.trim().isEmpty()) {
            throw new IllegalArgumentException("BGM 타입은 필수입니다");
        }
    }
} 