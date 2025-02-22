package com.j30n.stoblyx.application.dto.comment;

import com.j30n.stoblyx.common.dto.Command;
import com.j30n.stoblyx.common.dto.Query;
import com.j30n.stoblyx.common.dto.Response;
import com.j30n.stoblyx.common.dto.ValidationGroups;
import com.j30n.stoblyx.domain.model.comment.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 관련 DTO 클래스들
 */
public final class CommentDto {
    private CommentDto() {}

    // Command DTOs
    public static final class Commands {
        private Commands() {}

        /**
         * 댓글 생성 Command
         */
        public record Create(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotNull(message = "책 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "책 ID는 양수여야 합니다")
            Long bookId,

            @NotBlank(message = "댓글 내용은 필수입니다", groups = ValidationGroups.Create.class)
            @Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다")
            String content
        ) implements Command {
            public Create {
                if (content != null) content = content.trim();
            }
        }

        /**
         * 답글 생성 Command
         */
        public record CreateReply(
            @NotNull(message = "부모 댓글 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "부모 댓글 ID는 양수여야 합니다")
            Long parentId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotBlank(message = "답글 내용은 필수입니다", groups = ValidationGroups.Create.class)
            @Size(max = 1000, message = "답글 내용은 1000자를 초과할 수 없습니다")
            String content
        ) implements Command {
            public CreateReply {
                if (content != null) content = content.trim();
            }
        }

        /**
         * 댓글 수정 Command
         */
        public record Update(
            @NotNull(message = "댓글 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "댓글 ID는 양수여야 합니다")
            Long commentId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Update.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId,

            @NotBlank(message = "댓글 내용은 필수입니다", groups = ValidationGroups.Update.class)
            @Size(max = 1000, message = "댓글 내용은 1000자를 초과할 수 없습니다")
            String content
        ) implements Command {
            public Update {
                if (content != null) content = content.trim();
            }
        }

        /**
         * 댓글 삭제 Command
         */
        public record Delete(
            @NotNull(message = "댓글 ID는 필수입니다", groups = ValidationGroups.Delete.class)
            @Positive(message = "댓글 ID는 양수여야 합니다")
            Long commentId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Delete.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Command {}
    }

    // Query DTOs
    public static final class Queries {
        private Queries() {}

        /**
         * ID로 댓글 조회 Query
         */
        public record FindById(
            @NotNull(message = "댓글 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "댓글 ID는 양수여야 합니다")
            Long commentId
        ) implements Query {}

        /**
         * 인용구별 댓글 목록 조회 Query
         */
        public record FindByQuote(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId
        ) implements Query {}

        /**
         * 사용자별 댓글 목록 조회 Query
         */
        public record FindByUser(
            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Query {}
    }

    // Response DTOs
    public static final class Responses {
        private Responses() {}

        /**
         * 댓글 상세 정보 Response
         */
        public record CommentDetail(
            Long id,
            String content,
            Long quoteId,
            Long userId,
            String userName,
            Long bookId,
            Long parentId,
            int replyCount,
            boolean isDeleted,
            String status,
            LocalDateTime timestamp
        ) implements Response {
            public static CommentDetail from(Comment comment) {
                return new CommentDetail(
                    comment.getId().value(),
                    comment.getContent().value(),
                    comment.getQuote().getId().value(),
                    comment.getUser().getId(),
                    comment.getUser().getName(),
                    comment.getBookId().value(),
                    comment.getParent() != null ? comment.getParent().getId().value() : null,
                    comment.getReplies().size(),
                    comment.isDeleted(),
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