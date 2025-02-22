package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.domain.model.like.Like;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.in.like.FindLikeUseCase;
import com.j30n.stoblyx.domain.port.in.like.ToggleLikeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final ToggleLikeUseCase toggleLikeUseCase;
    private final FindLikeUseCase findLikeUseCase;

    /**
     * 좋아요 토글 API
     */
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(
        @RequestParam Long quoteId,
        @RequestParam Long userId
    ) {
        try {
            log.debug("좋아요 토글 요청 - 인용구 ID: {}, 사용자 ID: {}", quoteId, userId);

            Quote quote = Quote.withId(quoteId);
            User user = User.withId(userId);
            Like like = toggleLikeUseCase.toggleLike(quote, user);

            return ResponseEntity.ok(ApiResponse.success("좋아요가 토글되었습니다.", LikeResponse.from(like)));
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구별 좋아요 목록 조회 API
     */
    @GetMapping("/quote/{quoteId}")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> findByQuote(
        @PathVariable Long quoteId
    ) {
        try {
            log.debug("인용구별 좋아요 목록 조회 요청 - 인용구 ID: {}", quoteId);

            Quote quote = Quote.withId(quoteId);
            List<Like> likes = findLikeUseCase.findByQuote(quote);

            List<LikeResponse> response = likes.stream()
                .map(LikeResponse::from)
                .toList();
            return ResponseEntity.ok(ApiResponse.success("인용구별 좋아요 목록이 조회되었습니다.", response));
        } catch (Exception e) {
            log.error("인용구별 좋아요 목록 조회 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자별 좋아요 목록 조회 API
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> findByUser(
        @PathVariable Long userId
    ) {
        try {
            log.debug("사용자별 좋아요 목록 조회 요청 - 사용자 ID: {}", userId);

            User user = User.withId(userId);
            List<Like> likes = findLikeUseCase.findByUser(user);

            List<LikeResponse> response = likes.stream()
                .map(LikeResponse::from)
                .toList();
            return ResponseEntity.ok(ApiResponse.success("사용자별 좋아요 목록이 조회되었습니다.", response));
        } catch (Exception e) {
            log.error("사용자별 좋아요 목록 조회 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구의 좋아요 수 조회 API
     */
    @GetMapping("/count/{quoteId}")
    public ResponseEntity<ApiResponse<Long>> countActiveByQuote(
        @PathVariable Long quoteId
    ) {
        try {
            log.debug("인용구의 좋아요 수 조회 요청 - 인용구 ID: {}", quoteId);

            Quote quote = Quote.withId(quoteId);
            Long count = findLikeUseCase.countActiveByQuote(quote);

            return ResponseEntity.ok(ApiResponse.success("인용구의 좋아요 수가 조회되었습니다.", count));
        } catch (Exception e) {
            log.error("인용구의 좋아요 수 조회 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 좋아요 응답 DTO
     */
    public record LikeResponse(
        Long id,
        Long quoteId,
        Long userId,
        String userName,
        boolean isActive,
        String createdAt,
        String modifiedAt
    ) {
    public static LikeResponse from(Like like) {
        return new LikeResponse(
            like.getId(),                            // Long 타입 반환
            like.getQuote().getId().getValue(),      // QuoteId → Long 변환
            like.getUser().getId(),                  // Long 반환
            like.getUser().getName(),                // String 반환
            like.isActive(),                         // boolean 반환
            like.getCreatedAt().toString(),          // String 반환
            like.getModifiedAt().toString()          // String 반환
        );
    }
}
}
