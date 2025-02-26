package com.j30n.stoblyx.adapter.in.web.dto.quote;

import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.Book;
import java.time.LocalDateTime;

public record QuoteResponse(
    Long id,
    String content,
    String memo,
    int page,
    int likeCount,
    int saveCount,
    boolean isLiked,
    boolean isSaved,
    UserInfo user,
    BookInfo book,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public record UserInfo(
        Long id,
        String username,
        String nickname,
        String profileImageUrl
    ) {
        public static UserInfo from(User user) {
            return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getProfileImageUrl()
            );
        }
    }

    public record BookInfo(
        Long id,
        String title,
        String author,
        String thumbnailUrl
    ) {
        public static BookInfo from(Book book) {
            return new BookInfo(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getThumbnailUrl()
            );
        }
    }

    public static QuoteResponse from(Quote quote, boolean isLiked, boolean isSaved) {
        return new QuoteResponse(
            quote.getId(),
            quote.getContent(),
            quote.getMemo(),
            quote.getPage(),
            quote.getLikeCount(),
            quote.getSaveCount(),
            isLiked,
            isSaved,
            UserInfo.from(quote.getUser()),
            BookInfo.from(quote.getBook()),
            quote.getCreatedAt(),
            quote.getModifiedAt()
        );
    }
} 