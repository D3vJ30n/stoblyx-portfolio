package com.j30n.stoblyx.adapter.in.web.dto.bookmark;

import java.time.LocalDateTime;

public record BookmarkResponse(
    Long id,
    Long contentId,
    String title,
    String description,
    String thumbnailUrl,
    LocalDateTime createdAt
) {
} 