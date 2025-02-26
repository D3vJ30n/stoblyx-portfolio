package com.j30n.stoblyx.adapter.in.web.dto.like;

import com.j30n.stoblyx.domain.model.Like;
import java.time.LocalDateTime;

public record LikeResponse(
    Long id,
    Long userId,
    String username,
    Long quoteId,
    LocalDateTime createdAt
) {
    public static LikeResponse from(Like like) {
        return new LikeResponse(
            like.getId(),
            like.getUser().getId(),
            like.getUser().getUsername(),
            like.getQuote().getId(),
            like.getCreatedAt()
        );
    }
} 