package com.j30n.stoblyx.adapter.in.web.dto.post;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest(
    @NotBlank(message = "제목은 필수입니다.")
    String title,

    @NotBlank(message = "내용은 필수입니다.")
    String content,

    String thumbnailUrl
) {}
