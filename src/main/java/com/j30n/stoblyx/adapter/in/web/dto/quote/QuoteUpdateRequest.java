package com.j30n.stoblyx.adapter.in.web.dto.quote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;

/**
 * 문구 수정 요청 DTO
 */
public record QuoteUpdateRequest(
    @NotBlank(message = "문구 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "문구 내용은 1자 이상 1000자 이하여야 합니다")
    String content,

    @Size(max = 500, message = "메모는 500자 이하여야 합니다")
    String memo,

    @Max(value = 9999, message = "페이지는 9999 이하여야 합니다")
    Integer page,

    @Size(max = 100, message = "챕터는 100자 이하여야 합니다")
    String chapter
) {
    /**
     * 문구 수정 요청 DTO 생성자
     * 유효성 검사를 수행합니다.
     */
    public QuoteUpdateRequest {
        if (content != null) {
            content = content.trim();
        }
        if (memo != null) {
            memo = memo.trim();
        }
        if (chapter != null) {
            chapter = chapter.trim();
        }
    }
}