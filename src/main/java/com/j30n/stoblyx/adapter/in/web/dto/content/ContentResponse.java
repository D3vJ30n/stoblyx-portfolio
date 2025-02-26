package com.j30n.stoblyx.adapter.in.web.dto.content;

import com.j30n.stoblyx.domain.model.ContentStatus;
import com.j30n.stoblyx.domain.model.ShortFormContent;

import java.time.LocalDateTime;

public record ContentResponse(
    Long id,
    String videoUrl,
    String thumbnailUrl,
    String bgmUrl,
    String subtitles,
    ContentStatus status,
    int viewCount,
    int likeCount,
    int shareCount,
    boolean isLiked,
    boolean isBookmarked,
    BookInfo book,
    QuoteInfo quote,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public record BookInfo(
        Long id,
        String title,
        String author,
        String thumbnailUrl
    ) {
        public static BookInfo from(ShortFormContent content) {
            return new BookInfo(
                content.getBook().getId(),
                content.getBook().getTitle(),
                content.getBook().getAuthor(),
                content.getBook().getThumbnailUrl()
            );
        }
    }

    public record QuoteInfo(
        Long id,
        String content
    ) {
        public static QuoteInfo from(ShortFormContent content) {
            return new QuoteInfo(
                content.getQuote().getId(),
                content.getQuote().getContent()
            );
        }
    }

    public record UserInfo(
        Long id,
        String username,
        String nickname,
        String profileImageUrl
    ) {}

    public static ContentResponse from(ShortFormContent content, boolean isLiked, boolean isBookmarked) {
        return new ContentResponse(
            content.getId(),
            content.getVideoUrl(),
            content.getThumbnailUrl(),
            content.getBgmUrl(),
            content.getSubtitles(),
            content.getStatus(),
            content.getViewCount(),
            content.getLikeCount(),
            content.getShareCount(),
            isLiked,
            isBookmarked,
            BookInfo.from(content),
            QuoteInfo.from(content),
            content.getCreatedAt(),
            content.getModifiedAt()
        );
    }
} 