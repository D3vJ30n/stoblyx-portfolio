package com.j30n.stoblyx.adapter.in.web.dto.comment;

import com.j30n.stoblyx.domain.model.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    String content,
    Long userId,
    String username,
    Long quoteId,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            comment.getUser().getId(),
            comment.getUser().getUsername(),
            comment.getQuote().getId(),
            comment.getCreatedAt(),
            comment.getModifiedAt()
        );
    }
} 