package com.j30n.stoblyx.application.dto.quote;

import com.j30n.stoblyx.common.dto.Command;
import com.j30n.stoblyx.common.dto.Query;
import com.j30n.stoblyx.common.dto.Response;
import com.j30n.stoblyx.common.dto.ValidationGroups;
import com.j30n.stoblyx.domain.model.quote.Quote;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 인용구 관련 DTO 클래스들
 */
public final class QuoteDto {
    private QuoteDto() {
    }

    // Command DTOs
    public static final class Commands {
        private Commands() {
        }

        /**
         * 인용구 생성 Command
         */
        public record Create(
            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotBlank(message = "인용구 내용은 필수입니다", groups = ValidationGroups.Create.class)
            @Size(max = 5000, message = "인용구 내용은 5000자를 초과할 수 없습니다")
            String content,

            @NotNull(message = "페이지 번호는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "페이지 번호는 양수여야 합니다")
            Integer page
        ) implements Command {
            public Create {
                if (content != null) content = content.trim();
            }
        }

        /**
         * 인용구 내용 수정 Command
         */
        public record UpdateContent(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotBlank(message = "인용구 내용은 필수입니다", groups = ValidationGroups.Update.class)
            @Size(max = 5000, message = "인용구 내용은 5000자를 초과할 수 없습니다")
            String content
        ) implements Command {
            public UpdateContent {
                if (content != null) content = content.trim();
            }
        }

        /**
         * 인용구 페이지 수정 Command
         */
        public record UpdatePage(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotNull(message = "페이지 번호는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "페이지 번호는 양수여야 합니다")
            Integer page
        ) implements Command {
        }

        /**
         * 인용구 삭제 Command
         */
        public record Delete(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Delete.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Delete.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Command {
        }
    }

    // Query DTOs
    public static final class Queries {
        private Queries() {
        }

        /**
         * ID로 인용구 조회 Query
         */
        public record FindById(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId
        ) implements Query {
        }

        /**
         * 책별 인용구 목록 조회 Query
         */
        public record FindByBook(
            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId
        ) implements Query {
        }

        /**
         * 사용자별 인용구 목록 조회 Query
         */
        public record FindByUser(
            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Query {
        }

        public record CountByBook(
            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId
        ) implements Query {
        }
    }

    // Response DTOs
    public static final class Responses {
        private Responses() {
        }

        /**
         * 인용구 상세 정보 Response
         */
        public record QuoteDetail(
            Long id,
            String content,
            Integer page,
            Long bookId,
            String bookTitle,
            String bookAuthor,
            Long userId,
            String userName,
            int likeCount,
            int commentCount,
            List<Long> likeUserIds,
            String status,
            LocalDateTime timestamp
        ) implements Response {
            public static QuoteDetail from(Quote quote) {
                return new QuoteDetail(
                    quote.getId().getValue(),
                    quote.getContent().getValue(),
                    quote.getPage().getValue(),
                    quote.getBookId().getValue(),
                    quote.getBook().getTitle().getValue(), // 값 객체에서 값을 추출
                    quote.getBook().getAuthor().getValue(), // 값 객체에서 값을 추출 // .author() 대신 .getAuthor() 사용
                    quote.getUser().getId(),
                    quote.getUser().getName(),
                    quote.getLikes().size(),
                    quote.getComments().size(),
                    quote.getLikes().stream()
                        .map(like -> like.getUser().getId())
                        .toList(),
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
         * 인용구 목록 Response
         */
        public record QuoteSummary(
            Long id,
            String content,
            Integer page,
            Long bookId,
            String author,
            String title,
            Long userId,
            String userName,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
        ) implements Response {
            public static QuoteSummary from(Quote quote) {
                return new QuoteSummary(
                    quote.getId().getValue(),
                    quote.getContent().getValue(),
                    quote.getPage().getValue(),
                    quote.getBookId().getValue(),
                    quote.getBook().author().value(),
                    quote.getBook().title().value(),
                    quote.getUser().getId(),
                    quote.getUser().getName(),
                    quote.getCreatedAt(),
                    quote.getModifiedAt()
                );
            }

            @Override
            public String getStatus() {
                return "SUCCESS";
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.now();
            }
        }
    }
} 