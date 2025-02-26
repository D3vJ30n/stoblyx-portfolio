package com.j30n.stoblyx.adapter.in.web.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 댓글 생성 요청 DTO
 */
public record CommentCreateRequest(
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 1, max = 500, message = "댓글은 1자 이상 500자 이하로 작성해주세요")
    String content
) {
    /**
     * 댓글 생성 요청 DTO 생성자
     * 유효성 검사를 수행합니다.
     */
    public CommentCreateRequest {
        if (content != null) {
            content = content.trim();
        }
    }
}