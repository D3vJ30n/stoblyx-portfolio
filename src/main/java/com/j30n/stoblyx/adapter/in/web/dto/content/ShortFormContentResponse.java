package com.j30n.stoblyx.adapter.in.web.dto.content;

import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.enums.ContentType;
import com.j30n.stoblyx.domain.model.ShortFormContent;

import java.time.LocalDateTime;

/**
 * 숏폼 콘텐츠 응답을 위한 DTO
 */
public record ShortFormContentResponse(
    Long id,
    String title,
    String description,
    ContentStatus status,
    int viewCount,
    int likeCount,
    int shareCount,
    int commentCount,
    ContentType contentType,
    String videoUrl,
    String thumbnailUrl,
    String audioUrl,
    String subtitles,
    int duration,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    String bookThumbnailUrl,
    LocalDateTime createdAt
) {
    /**
     * ShortFormContent 엔티티를 ShortFormContentResponse DTO로 변환합니다.
     *
     * @param content 숏폼 콘텐츠 엔티티
     * @return ShortFormContentResponse DTO
     */
    public static ShortFormContentResponse from(ShortFormContent content) {
        return new ShortFormContentResponse(
            content.getId(),
            content.getTitle(),
            content.getDescription(),
            content.getStatus(),
            content.getViewCount(),
            content.getLikeCount(),
            content.getShareCount(),
            content.getCommentCount(),
            content.getContentType(),
            content.getVideoUrl(),
            content.getThumbnailUrl(),
            content.getAudioUrl(),
            content.getSubtitles(),
            content.getDuration(),
            content.getBook() != null ? content.getBook().getId() : null,
            content.getBook() != null ? content.getBook().getTitle() : null,
            content.getBook() != null ? content.getBook().getAuthor() : null,
            content.getBook() != null ? content.getBook().getThumbnailUrl() : null,
            content.getCreatedAt()
        );
    }
} 