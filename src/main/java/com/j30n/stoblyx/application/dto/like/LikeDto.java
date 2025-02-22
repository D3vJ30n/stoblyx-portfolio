package com.j30n.stoblyx.application.dto.like;

import com.j30n.stoblyx.common.dto.Command;
import com.j30n.stoblyx.common.dto.Query;
import com.j30n.stoblyx.common.dto.Response;
import com.j30n.stoblyx.common.dto.ValidationGroups;
import com.j30n.stoblyx.domain.model.like.Like;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * 좋아요 관련 DTO 클래스들
 */
public final class LikeDto {
    private LikeDto() {}

    // Command DTOs
    public static final class Commands {
        private Commands() {}

        /**
         * 좋아요 토글 Command
         */
        public record Toggle(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Create.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Command {}
    }

    // Query DTOs
    public static final class Queries {
        private Queries() {}

        /**
         * ID로 좋아요 조회 Query
         */
        public record FindById(
            @NotNull(message = "좋아요 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "좋아요 ID는 양수여야 합니다")
            Long likeId
        ) implements Query {}

        /**
         * 인용구별 좋아요 목록 조회 Query
         */
        public record FindByQuote(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId
        ) implements Query {}

        /**
         * 사용자별 좋아요 목록 조회 Query
         */
        public record FindByUser(
            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Query {}

        /**
         * 인용구와 사용자별 좋아요 조회 Query
         */
        public record FindByQuoteAndUser(
            @NotNull(message = "인용구 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "인용구 ID는 양수여야 합니다")
            Long quoteId,

            @NotNull(message = "사용자 ID는 필수입니다", groups = ValidationGroups.Read.class)
            @Positive(message = "사용자 ID는 양수여야 합니다")
            Long userId
        ) implements Query {}
    }

    // Response DTOs
    public static final class Responses {
        private Responses() {}

        /**
         * 좋아요 상세 정보 Response
         */
        public record LikeDetail(
            Long id,
            Long quoteId,
            Long userId,
            String userName,
            boolean isActive,
            String status,
            LocalDateTime timestamp
        ) implements Response {
            public static LikeDetail from(Like like) {
                return new LikeDetail(
                    like.getId().value(),
                    like.getQuote().getId().value(),
                    like.getUser().getId(),
                    like.getUser().getName(),
                    like.isActive(),
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