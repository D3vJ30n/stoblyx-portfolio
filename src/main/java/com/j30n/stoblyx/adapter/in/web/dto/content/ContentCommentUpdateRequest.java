package com.j30n.stoblyx.adapter.in.web.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 콘텐츠 댓글 수정 요청 DTO
 */
public record ContentCommentUpdateRequest(
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 500, message = "댓글 내용은 1자 이상 500자 이하여야 합니다.")
    String commentText
) {
    // 컴팩트 생성자로 유효성 검증
    public ContentCommentUpdateRequest {
        if (commentText != null) {
            commentText = commentText.trim();
        }
    }
} 