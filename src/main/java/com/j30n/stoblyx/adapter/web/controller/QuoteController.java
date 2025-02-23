package com.j30n.stoblyx.adapter.web.controller;

import com.j30n.stoblyx.adapter.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.application.service.quote.QuoteService;
import com.j30n.stoblyx.common.annotation.CurrentUser;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponse>> createQuote(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody QuoteCreateRequest request) {
        try {
            QuoteResponse response = quoteService.createQuote(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "문구가 성공적으로 생성되었습니다.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> getQuote(@PathVariable Long id) {
        try {
            QuoteResponse response = quoteService.getQuote(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "문구를 성공적으로 조회했습니다.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuoteResponse>>> getQuotes(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long bookId,
        Pageable pageable) {
        try {
            Page<QuoteResponse> response = quoteService.getQuotes(userId, bookId, pageable);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "문구 목록을 성공적으로 조회했습니다.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponse>> updateQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id,
        @Valid @RequestBody QuoteCreateRequest request) {
        try {
            QuoteResponse response = quoteService.updateQuote(currentUser.getId(), id, request);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "문구가 성공적으로 수정되었습니다.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuote(
        @CurrentUser UserPrincipal currentUser,
        @PathVariable Long id) {
        try {
            quoteService.deleteQuote(currentUser.getId(), id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "문구가 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 