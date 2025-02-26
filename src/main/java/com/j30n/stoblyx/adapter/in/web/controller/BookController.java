package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.service.book.BookService;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 책 관련 API를 처리하는 컨트롤러
 * 책의 등록, 조회, 수정, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 새로운 책을 등록합니다.
     *
     * @param request 책 등록 요청 DTO
     * @return 등록된 책 정보
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
        @Valid @RequestBody BookCreateRequest request
    ) {
        try {
            BookResponse response = bookService.createBook(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("책이 성공적으로 등록되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * ID로 책을 조회합니다.
     *
     * @param id 조회할 책의 ID
     * @return 조회된 책 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable Long id) {
        try {
            BookResponse response = bookService.getBook(id);
            return ResponseEntity.ok(ApiResponse.success("책 조회에 성공했습니다.", response));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 책 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @param genre 장르 필터 (선택)
     * @return 조회된 책 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getBooks(
        @RequestParam(required = false) String genre,
        @PageableDefault Pageable pageable
    ) {
        Page<BookResponse> response = genre != null ? 
            bookService.findByGenre(genre, pageable) : 
            bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success("책 목록 조회에 성공했습니다.", response));
    }

    /**
     * 책 정보를 수정합니다.
     *
     * @param id 수정할 책의 ID
     * @param request 수정할 내용
     * @return 수정된 책 정보
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookCreateRequest request
    ) {
        try {
            BookResponse response = bookService.updateBook(id, request);
            return ResponseEntity.ok(ApiResponse.success("책이 성공적으로 수정되었습니다.", response));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 책을 삭제합니다.
     *
     * @param id 삭제할 책의 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(ApiResponse.success("책이 성공적으로 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}