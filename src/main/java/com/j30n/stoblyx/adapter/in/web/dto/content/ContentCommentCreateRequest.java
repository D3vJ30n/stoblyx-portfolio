package com.j30n.stoblyx.adapter.in.web.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 콘텐츠 댓글 생성 요청 DTO
 */
public record ContentCommentCreateRequest(
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 500, message = "댓글 내용은 1자 이상 500자 이하여야 합니다.")
    String commentText,

    Long parentId
) {
    // 컴팩트 생성자로 유효성 검증
    public ContentCommentCreateRequest {
        if (commentText != null) {
            commentText = commentText.trim();
        }
    }
} 