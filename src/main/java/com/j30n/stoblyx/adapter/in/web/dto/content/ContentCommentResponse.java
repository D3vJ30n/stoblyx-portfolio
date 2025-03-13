package com.j30n.stoblyx.adapter.in.web.dto.content;

import com.j30n.stoblyx.domain.model.ContentComment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 콘텐츠 댓글 응답 DTO
 */
public record ContentCommentResponse(
    Long id,
    String commentText,
    UserInfo user,
    Long contentId,
    Long parentId,
    int likeCount,
    List<ContentCommentResponse> replies,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    /**
     * 사용자 정보 내부 클래스
     */
    public record UserInfo(
        Long id,
        String username,
        String nickname,
        String profileImageUrl
    ) {
        /**
         * 사용자 엔티티에서 UserInfo 생성
         */
        public static UserInfo from(com.j30n.stoblyx.domain.model.User user) {
            return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getProfileImageUrl()
            );
        }
    }

    /**
     * 댓글 엔티티에서 응답 DTO 생성 (대댓글 없음)
     */
    public static ContentCommentResponse from(ContentComment comment) {
        return new ContentCommentResponse(
            comment.getId(),
            comment.getContent(),
            UserInfo.from(comment.getUser()),
            comment.getShortFormContent().getId(),
            null, // 부모 댓글 참조 필드가 없음
            0, // likeCount 필드가 없음
            List.of(),
            comment.getCreatedAt(),
            comment.getModifiedAt()
        );
    }

    /**
     * 댓글 엔티티에서 응답 DTO 생성 (대댓글 포함)
     */
    public static ContentCommentResponse fromWithReplies(ContentComment comment, List<ContentComment> replies) {
        return new ContentCommentResponse(
            comment.getId(),
            comment.getContent(),
            UserInfo.from(comment.getUser()),
            comment.getShortFormContent().getId(),
            null, // 부모 댓글 참조 필드가 없음
            0, // likeCount 필드가 없음
            replies.stream()
                .map(ContentCommentResponse::from)
                .toList(),
            comment.getCreatedAt(),
            comment.getModifiedAt()
        );
    }
} 