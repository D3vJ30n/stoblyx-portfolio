package com.j30n.stoblyx.adapter.web.controller;

import com.j30n.stoblyx.adapter.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.service.book.BookService;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
        @Valid @RequestBody BookCreateRequest request
    ) {
        try {
            BookResponse response = bookService.createBook(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("SUCCESS", "책이 성공적으로 등록되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
        @PathVariable Long id
    ) {
        try {
            BookResponse response = bookService.getBook(id);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "책 정보를 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getBooks(
        @RequestParam(required = false) String searchKeyword,
        Pageable pageable
    ) {
        try {
            Page<BookResponse> response = bookService.getBooks(searchKeyword, pageable);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "책 목록을 성공적으로 조회했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookCreateRequest request
    ) {
        try {
            BookResponse response = bookService.updateBook(id, request);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "책 정보가 성공적으로 수정되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
        @PathVariable Long id
    ) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok()
                .body(new ApiResponse<>("SUCCESS", "책이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 