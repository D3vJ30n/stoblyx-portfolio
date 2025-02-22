package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.application.dto.quote.QuoteDto;
import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.common.utils.SecurityUtils;
import com.j30n.stoblyx.domain.model.book.BookId;
import com.j30n.stoblyx.domain.model.quote.Content;
import com.j30n.stoblyx.domain.model.quote.Page;
import com.j30n.stoblyx.domain.model.quote.Quote;
import com.j30n.stoblyx.domain.model.quote.QuoteId;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.in.quote.CreateQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.quote.DeleteQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.quote.FindQuoteUseCase;
import com.j30n.stoblyx.domain.port.in.quote.UpdateQuoteUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 인용구 관련 API를 처리하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final CreateQuoteUseCase createQuoteUseCase;
    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final DeleteQuoteUseCase deleteQuoteUseCase;
    private final FindQuoteUseCase findQuoteUseCase;
    private final SecurityUtils securityUtils;

    /**
     * 새로운 인용구를 생성합니다.
     *
     * @param request 인용구 생성 요청 정보
     * @return 생성된 인용구 정보
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuoteDto.Responses.QuoteDetail>> createQuote(
        @Valid @RequestBody CreateQuoteRequest request
    ) {
        try {
            User user = securityUtils.getCurrentUser();
            Quote quote = createQuoteUseCase.createQuote(
                new Content(request.content()),
                new Page(request.page()),
                new BookId(request.bookId()),
                user
            );

            return ResponseEntity.ok(ApiResponse.success(
                "인용구가 생성되었습니다",
                QuoteDto.Responses.QuoteDetail.from(quote)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구 내용을 수정합니다.
     *
     * @param id      수정할 인용구 ID
     * @param request 인용구 내용 수정 요청 정보
     * @return 수정된 인용구 정보
     */
    @PutMapping("/{id}/content")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuoteDto.Responses.QuoteDetail>> updateContent(
        @PathVariable Long id,
        @Valid @RequestBody UpdateQuoteContentRequest request
    ) {
        try {
            User user = securityUtils.getCurrentUser();
            Quote quote = updateQuoteUseCase.updateContent(
                new QuoteId(id),
                new Content(request.content()),
                user
            );

            return ResponseEntity.ok(ApiResponse.success(
                "인용구 내용이 수정되었습니다",
                QuoteDto.Responses.QuoteDetail.from(quote)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구 페이지를 수정합니다.
     *
     * @param id      수정할 인용구 ID
     * @param request 인용구 페이지 수정 요청 정보
     * @return 수정된 인용구 정보
     */
    @PutMapping("/{id}/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuoteDto.Responses.QuoteDetail>> updatePage(
        @PathVariable Long id,
        @Valid @RequestBody UpdateQuotePageRequest request
    ) {
        try {
            User user = securityUtils.getCurrentUser();
            Quote quote = updateQuoteUseCase.updatePage(
                new QuoteId(id),
                new Page(request.page()),
                user
            );

            return ResponseEntity.ok(ApiResponse.success(
                "인용구 페이지가 수정되었습니다",
                QuoteDto.Responses.QuoteDetail.from(quote)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구를 삭제합니다.
     *
     * @param id 삭제할 인용구 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteQuote(@PathVariable Long id) {
        try {
            User user = securityUtils.getCurrentUser();
            deleteQuoteUseCase.deleteQuote(new QuoteId(id), user);
            return ResponseEntity.ok(ApiResponse.success("인용구가 삭제되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * ID로 인용구를 조회합니다.
     *
     * @param id 인용구 ID
     * @return 조회된 인용구 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteDto.Responses.QuoteDetail>> getQuote(@PathVariable Long id) {
        try {
            Quote quote = findQuoteUseCase.findById(new QuoteId(id))
                .orElseThrow(() -> new IllegalArgumentException("인용구를 찾을 수 없습니다"));

            return ResponseEntity.ok(ApiResponse.success(
                "인용구가 조회되었습니다",
                QuoteDto.Responses.QuoteDetail.from(quote)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 책에 속한 인용구 목록을 조회합니다.
     *
     * @param bookId 책 ID
     * @return 인용구 목록
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<QuoteDto.Responses.QuoteSummary>>> getQuotesByBook(
        @PathVariable Long bookId
    ) {
        try {
            List<Quote> quotes = findQuoteUseCase.findByBookId(new BookId(bookId));
            List<QuoteDto.Responses.QuoteSummary> response = quotes.stream()
                .map(QuoteDto.Responses.QuoteSummary::from)
                .toList();

            return ResponseEntity.ok(ApiResponse.success(
                "책별 인용구 목록이 조회되었습니다",
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 사용자가 작성한 인용구 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 인용구 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<QuoteDto.Responses.QuoteSummary>>> getQuotesByUser(
        @PathVariable Long userId
    ) {
        try {
            User user = User.withId(userId);
            List<Quote> quotes = findQuoteUseCase.findByUser(user);
            List<QuoteDto.Responses.QuoteSummary> response = quotes.stream()
                .map(QuoteDto.Responses.QuoteSummary::from)
                .toList();

            return ResponseEntity.ok(ApiResponse.success(
                "사용자별 인용구 목록이 조회되었습니다",
                response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 책의 인용구 수를 조회합니다.
     *
     * @param bookId 책 ID
     * @return 인용구 수
     */
    @GetMapping("/count/{bookId}")
    public ResponseEntity<ApiResponse<Long>> countQuotesByBook(@PathVariable Long bookId) {
        try {
            long count = findQuoteUseCase.countByBookId(new BookId(bookId));
            return ResponseEntity.ok(ApiResponse.success(
                "책의 인용구 수가 조회되었습니다",
                count
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 인용구 생성 요청 DTO
     */
    public record CreateQuoteRequest(
        @NotNull(message = "책 ID는 필수입니다")
        @Positive(message = "책 ID는 양수여야 합니다")
        Long bookId,

        @NotBlank(message = "인용구 내용은 필수입니다")
        @Size(max = 5000, message = "인용구 내용은 5000자를 초과할 수 없습니다")
        String content,

        @NotNull(message = "페이지 번호는 필수입니다")
        @Positive(message = "페이지 번호는 양수여야 합니다")
        Integer page
    ) {
    }

    /**
     * 인용구 내용 수정 요청 DTO
     */
    public record UpdateQuoteContentRequest(
        @NotBlank(message = "인용구 내용은 필수입니다")
        @Size(max = 5000, message = "인용구 내용은 5000자를 초과할 수 없습니다")
        String content
    ) {
    }

    /**
     * 인용구 페이지 수정 요청 DTO
     */
    public record UpdateQuotePageRequest(
        @NotNull(message = "페이지 번호는 필수입니다")
        @Positive(message = "페이지 번호는 양수여야 합니다")
        Integer page
    ) {
    }
}
 