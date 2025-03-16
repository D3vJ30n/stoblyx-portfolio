package com.j30n.stoblyx.adapter.in.web.dto.content;

import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.model.MediaResource;
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
    public static ContentResponse from(ShortFormContent content, boolean isLiked, boolean isBookmarked) {
        // 미디어 리소스에서 필요한 URL 추출
        String videoUrl = content.getMediaResources().stream()
            .filter(resource -> resource.getType() == MediaResource.MediaType.VIDEO)
            .findFirst()
            .map(MediaResource::getUrl)
            .orElse(null);

        String thumbnailUrl = content.getMediaResources().stream()
            .filter(resource -> resource.getType() == MediaResource.MediaType.IMAGE)
            .findFirst()
            .map(MediaResource::getThumbnailUrl)
            .orElse(null);

        String bgmUrl = content.getMediaResources().stream()
            .filter(resource -> resource.getType() == MediaResource.MediaType.BGM)
            .findFirst()
            .map(MediaResource::getUrl)
            .orElse(null);

        String subtitles = content.getMediaResources().stream()
            .filter(resource -> resource.getType() == MediaResource.MediaType.SUBTITLE)
            .findFirst()
            .map(MediaResource::getDescription)
            .orElse(null);

        return new ContentResponse(
            content.getId(),
            videoUrl,
            thumbnailUrl,
            bgmUrl,
            subtitles,
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

    public static ContentResponseBuilder builder() {
        return new ContentResponseBuilder();
    }

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
    ) {
    }

    public static class ContentResponseBuilder {
        private Long id;
        private String videoUrl;
        private String thumbnailUrl;
        private String bgmUrl;
        private String subtitles;
        private ContentStatus status;
        private int viewCount;
        private int likeCount;
        private int shareCount;
        private boolean isLiked;
        private boolean isBookmarked;
        private BookInfo book;
        private QuoteInfo quote;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public ContentResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ContentResponseBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public ContentResponseBuilder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public ContentResponseBuilder bgmUrl(String bgmUrl) {
            this.bgmUrl = bgmUrl;
            return this;
        }

        public ContentResponseBuilder subtitles(String subtitles) {
            this.subtitles = subtitles;
            return this;
        }

        public ContentResponseBuilder status(ContentStatus status) {
            this.status = status;
            return this;
        }

        public ContentResponseBuilder viewCount(int viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public ContentResponseBuilder likeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public ContentResponseBuilder shareCount(int shareCount) {
            this.shareCount = shareCount;
            return this;
        }

        public ContentResponseBuilder isLiked(boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public ContentResponseBuilder isBookmarked(boolean isBookmarked) {
            this.isBookmarked = isBookmarked;
            return this;
        }

        public ContentResponseBuilder book(BookInfo book) {
            this.book = book;
            return this;
        }

        public ContentResponseBuilder quote(QuoteInfo quote) {
            this.quote = quote;
            return this;
        }

        public ContentResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ContentResponseBuilder modifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public ContentResponse build() {
            return new ContentResponse(id, videoUrl, thumbnailUrl, bgmUrl, subtitles, status, viewCount, likeCount,
                shareCount, isLiked, isBookmarked, book, quote, createdAt, modifiedAt);
        }
    }
} 