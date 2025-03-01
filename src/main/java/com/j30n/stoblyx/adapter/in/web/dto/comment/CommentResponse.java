package com.j30n.stoblyx.adapter.in.web.dto.comment;

import com.j30n.stoblyx.domain.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private UserInfo user;
    private Long quoteId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String profileImage;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            new UserInfo(
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getNickname(),
                comment.getUser().getProfileImageUrl()
            ),
            comment.getQuote().getId(),
            comment.getCreatedAt(),
            comment.getModifiedAt()
        );
    }
} 