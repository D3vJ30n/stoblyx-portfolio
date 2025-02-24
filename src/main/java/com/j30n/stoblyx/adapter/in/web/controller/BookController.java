package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.book.BookCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.service.book.BookService;
import com.j30n.stoblyx.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 책 관련 API를 처리하는 컨트롤러
 * 책의 등록, 조회, 수정, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/books")
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
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
        @Valid @RequestBody BookCreateRequest request
    ) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("SUCCESS", "책이 성공적으로 등록되었습니다.", response));
    }

    /**
     * ID로 책을 조회합니다.
     *
     * @param id 조회할 책의 ID
     * @return 조회된 책 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
        @PathVariable Long id
    ) {
        BookResponse response = bookService.getBook(id);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "책 정보를 성공적으로 조회했습니다.", response));
    }

    /**
     * 책 목록을 검색어와 함께 페이징하여 조회합니다.
     *
     * @param searchKeyword 검색어 (선택)
     * @param pageable 페이징 정보 (기본값: page=0, size=10, sort=id,desc)
     * @return 페이징된 책 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookResponse>>> getBooks(
        @RequestParam(required = false) String searchKeyword,
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<BookResponse> response = bookService.getBooks(searchKeyword, pageable);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "책 목록을 성공적으로 조회했습니다.", response));
    }

    /**
     * 책 정보를 수정합니다.
     *
     * @param id 수정할 책의 ID
     * @param request 수정할 책 정보
     * @return 수정된 책 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookCreateRequest request
    ) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "책 정보가 성공적으로 수정되었습니다.", response));
    }

    /**
     * 책을 삭제합니다.
     *
     * @param id 삭제할 책의 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
        @PathVariable Long id
    ) {
        bookService.deleteBook(id);
        return ResponseEntity.ok()
            .body(new ApiResponse<>("SUCCESS", "책이 성공적으로 삭제되었습니다.", null));
    }
} 