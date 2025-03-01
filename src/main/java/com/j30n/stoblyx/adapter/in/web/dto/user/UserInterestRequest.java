package com.j30n.stoblyx.adapter.in.web.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserInterestRequest(
    @NotEmpty(message = "관심 장르는 최소 1개 이상 선택해야 합니다")
    @Size(max = 5, message = "관심 장르는 최대 5개까지 선택 가능합니다")
    List<String> genres,

    @NotEmpty(message = "관심 작가는 최소 1개 이상 선택해야 합니다")
    @Size(max = 5, message = "관심 작가는 최대 5개까지 선택 가능합니다")
    List<String> authors,

    @NotEmpty(message = "관심 키워드는 최소 1개 이상 선택해야 합니다")
    @Size(max = 5, message = "관심 키워드는 최대 5개까지 선택 가능합니다")
    List<String> keywords,

    @Size(max = 500, message = "소개글은 500자를 초과할 수 없습니다")
    String bio
) {
    public UserInterestRequest {
        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("관심 장르는 최소 1개 이상 선택해야 합니다");
        }
        if (authors == null || authors.isEmpty()) {
            throw new IllegalArgumentException("관심 작가는 최소 1개 이상 선택해야 합니다");
        }
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("관심 키워드는 최소 1개 이상 선택해야 합니다");
        }
        if (bio != null && bio.length() > 500) {
            throw new IllegalArgumentException("소개글은 500자를 초과할 수 없습니다");
        }
    }
}