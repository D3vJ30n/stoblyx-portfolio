package com.j30n.stoblyx.adapter.in.web.dto.like;

import com.j30n.stoblyx.domain.model.Like;
import java.time.LocalDateTime;

/**
 * 좋아요 응답 DTO
 * Like 엔티티는 복합 키(user_id, quote_id)를 사용하므로, 
 * 별도의 단일 ID 필드가 없습니다.
 */
public record LikeResponse(
    Long userId,
    String username,
    Long quoteId,
    LocalDateTime createdAt
) {
    public static LikeResponse from(Like like) {
        return new LikeResponse(
            like.getUser().getId(),
            like.getUser().getUsername(),
            like.getQuote().getId(),
            like.getCreatedAt()
        );
    }
    
    public static LikeResponseBuilder builder() {
        return new LikeResponseBuilder();
    }
    
    public static class LikeResponseBuilder {
        private Long userId;
        private String username;
        private Long quoteId;
        private LocalDateTime createdAt;
        
        public LikeResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public LikeResponseBuilder username(String username) { this.username = username; return this; }
        public LikeResponseBuilder quoteId(Long quoteId) { this.quoteId = quoteId; return this; }
        public LikeResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        
        public LikeResponse build() {
            return new LikeResponse(userId, username, quoteId, createdAt);
        }
    }
} 