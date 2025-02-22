package com.j30n.stoblyx.application.dto.book;

import com.j30n.stoblyx.common.dto.Command;
import com.j30n.stoblyx.common.dto.Query;
import com.j30n.stoblyx.common.dto.Response;
import com.j30n.stoblyx.common.dto.ValidationGroups;
import com.j30n.stoblyx.domain.model.book.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 책 관련 DTO 클래스들
 */
public final class BookDto {
    private BookDto() {}

    // Command DTOs
    public static final class Commands {
        private Commands() {}

        /**
         * 책 생성 Command
         */
        public record Create(
            @NotBlank(message = "책 제목은 필수입니다", groups = ValidationGroups.Create.class)
            @Size(max = 200, message = "책 제목은 200자를 초과할 수 없습니다")
            String title,

            @NotBlank(message = "저자는 필수입니다", groups = ValidationGroups.Create.class)
            @Size(max = 100, message = "저자는 100자를 초과할 수 없습니다")
            String author,

            @Size(max = 13, message = "ISBN은 13자를 초과할 수 없습니다")
            String isbn,

            @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
            String description,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Command {
            public Create {
                if (title != null) title = title.trim();
                if (author != null) author = author.trim();
                if (isbn != null) isbn = isbn.trim();
                if (description != null) description = description.trim();
            }
        }

        /**
         * 책 정보 수정 Command
         */
        public record Update(
            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotBlank(message = "책 제목은 필수입니다", groups = ValidationGroups.Update.class)
            @Size(max = 200, message = "책 제목은 200자를 초과할 수 없습니다")
            String title,

            @NotBlank(message = "저자는 필수입니다", groups = ValidationGroups.Update.class)
            @Size(max = 100, message = "저자는 100자를 초과할 수 없습니다")
            String author,

            @Size(max = 13, message = "ISBN은 13자를 초과할 수 없습니다")
            String isbn,

            @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
            String description
        ) implements Command {
            public Update {
                if (title != null) title = title.trim();
                if (author != null) author = author.trim();
                if (isbn != null) isbn = isbn.trim();
                if (description != null) description = description.trim();
            }
        }

        /**
         * 책 삭제 Command
         */
        public record Delete(
            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Delete.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Delete.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Command {}
    }

    // Query DTOs
    public static final class Queries {
        private Queries() {}

        /**
         * ID로 책 조회 Query
         */
        public record FindById(
            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId
        ) implements Query {}

        /**
         * 사용자별 책 목록 조회 Query
         */
        public record FindByUser(
            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Query {}

        /**
         * ISBN으로 책 조회 Query
         */
        public record FindByIsbn(
            @NotBlank(message = "ISBN은 필수입니다", groups = ValidationGroups.Read.class)
            @Size(max = 13, message = "ISBN은 13자를 초과할 수 없습니다")
            String isbn
        ) implements Query {
            public FindByIsbn {
                if (isbn != null) isbn = isbn.trim();
            }
        }
    }

    // Response DTOs
    public static final class Responses {
        private Responses() {}

        /**
         * 책 상세 정보 Response
         */
        public record BookDetail(
            Long id,
            String title,
            String author,
            String isbn,
            String description,
            Long userId,
            String userName,
            int quoteCount,
            String status,
            LocalDateTime timestamp
        ) implements Response {
            public static BookDetail from(Book book) {
                return new BookDetail(
                    book.getId().value(),
                    book.getTitle().value(),
                    book.getAuthor().value(),
                    book.getIsbn().value(),
                    book.getDescription().value(),
                    book.getUser().getId(),
                    book.getUser().getName(),
                    book.getQuotes().size(),
                    "SUCCESS",
                    LocalDateTime.now()
                );
            }

            @Override
            public String getStatus() {
                return status;
            }

            @Override
            public LocalDateTime getTimestamp() {
                return timestamp;
            }
        }

        /**
         * 책 목록 Response
         */
        public record BookSummary(
            Long id,
            String title,
            String author,
            int quoteCount,
            String status,
            LocalDateTime timestamp
        ) implements Response {
            public static BookSummary from(Book book) {
                return new BookSummary(
                    book.getId().value(),
                    book.getTitle().value(),
                    book.getAuthor().value(),
                    book.getQuotes().size(),
                    "SUCCESS",
                    LocalDateTime.now()
                );
            }

            @Override
            public String getStatus() {
                return status;
            }

            @Override
            public LocalDateTime getTimestamp() {
                return timestamp;
            }
        }
    }
} 