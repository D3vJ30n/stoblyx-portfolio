package com.j30n.stoblyx.adapter.in.web.dto.quote;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;

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

    public static QuoteResponseBuilder builder() {
        return new QuoteResponseBuilder();
    }

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

        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }

        public static class UserInfoBuilder {
            private Long id;
            private String username;
            private String nickname;
            private String profileImageUrl;

            public UserInfoBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public UserInfoBuilder username(String username) {
                this.username = username;
                return this;
            }

            public UserInfoBuilder nickname(String nickname) {
                this.nickname = nickname;
                return this;
            }

            public UserInfoBuilder profileImageUrl(String profileImageUrl) {
                this.profileImageUrl = profileImageUrl;
                return this;
            }

            public UserInfo build() {
                return new UserInfo(id, username, nickname, profileImageUrl);
            }
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

        public static BookInfoBuilder builder() {
            return new BookInfoBuilder();
        }

        public static class BookInfoBuilder {
            private Long id;
            private String title;
            private String author;
            private String thumbnailUrl;

            public BookInfoBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public BookInfoBuilder title(String title) {
                this.title = title;
                return this;
            }

            public BookInfoBuilder author(String author) {
                this.author = author;
                return this;
            }

            public BookInfoBuilder thumbnailUrl(String thumbnailUrl) {
                this.thumbnailUrl = thumbnailUrl;
                return this;
            }

            public BookInfo build() {
                return new BookInfo(id, title, author, thumbnailUrl);
            }
        }
    }

    public static class QuoteResponseBuilder {
        private Long id;
        private String content;
        private String memo;
        private int page;
        private int likeCount;
        private int saveCount;
        private boolean isLiked;
        private boolean isSaved;
        private UserInfo user;
        private BookInfo book;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public QuoteResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuoteResponseBuilder content(String content) {
            this.content = content;
            return this;
        }

        public QuoteResponseBuilder memo(String memo) {
            this.memo = memo;
            return this;
        }

        public QuoteResponseBuilder page(int page) {
            this.page = page;
            return this;
        }

        public QuoteResponseBuilder likeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public QuoteResponseBuilder saveCount(int saveCount) {
            this.saveCount = saveCount;
            return this;
        }

        public QuoteResponseBuilder isLiked(boolean isLiked) {
            this.isLiked = isLiked;
            return this;
        }

        public QuoteResponseBuilder isSaved(boolean isSaved) {
            this.isSaved = isSaved;
            return this;
        }

        public QuoteResponseBuilder user(UserInfo user) {
            this.user = user;
            return this;
        }

        public QuoteResponseBuilder book(BookInfo book) {
            this.book = book;
            return this;
        }

        public QuoteResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public QuoteResponseBuilder modifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public QuoteResponse build() {
            return new QuoteResponse(id, content, memo, page, likeCount, saveCount, isLiked, isSaved, user, book, createdAt, modifiedAt);
        }
    }
} 