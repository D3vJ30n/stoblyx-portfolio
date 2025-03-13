package com.j30n.stoblyx.adapter.in.web.dto.post;

import com.j30n.stoblyx.domain.model.Post;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    String title,
    String content,
    String authorName,
    String thumbnailUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getUser().getNickname(),
            post.getThumbnailUrl(),
            post.getCreatedAt(),
            post.getModifiedAt()
        );
    }
    
    // 빌더 패턴 추가
    public static PostResponseBuilder builder() {
        return new PostResponseBuilder();
    }
    
    public static class PostResponseBuilder {
        private Long id;
        private String title;
        private String content;
        private String authorName;
        private String thumbnailUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public PostResponseBuilder id(Long id) { this.id = id; return this; }
        public PostResponseBuilder title(String title) { this.title = title; return this; }
        public PostResponseBuilder content(String content) { this.content = content; return this; }
        public PostResponseBuilder authorName(String authorName) { this.authorName = authorName; return this; }
        public PostResponseBuilder thumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; return this; }
        public PostResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PostResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public PostResponse build() {
            return new PostResponse(id, title, content, authorName, thumbnailUrl, createdAt, updatedAt);
        }
    }
}
