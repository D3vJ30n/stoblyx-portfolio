package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.infrastructure.external.AladinApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알라딘 API 테스트를 위한 컨트롤러
 * 실제 알라딘 API를 호출하여 결과를 바로 확인할 수 있는 테스트용 엔드포인트 제공
 */
@Slf4j
@RestController
@RequestMapping("/aladin/test")
@RequiredArgsConstructor
public class AladinTestController {

    private final AladinApiClient aladinApiClient;

    /**
     * 키워드로 알라딘 API 검색 결과를 가져옵니다.
     * 예: /api/aladin/test/search?keyword=동기부여
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(@RequestParam String keyword) {
        log.info("알라딘 API 키워드 검색 테스트: {}", keyword);
        try {
            List<Book> books = aladinApiClient.searchBooks(keyword);
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("알라딘 API에서 키워드 '%s'로 검색한 결과입니다. 총 %d개", keyword, books.size()),
                    books));
        } catch (Exception e) {
            log.error("알라딘 API 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("알라딘 API 검색 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 알라딘 API에서 신간 도서를 가져옵니다.
     */
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<Book>>> getNewBooks() {
        log.info("알라딘 API 신간 도서 테스트");
        try {
            List<Book> books = aladinApiClient.getNewBooks();
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("알라딘 API에서 신간 도서 목록을 가져왔습니다. 총 %d개", books.size()),
                    books));
        } catch (Exception e) {
            log.error("알라딘 API 신간 도서 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("알라딘 API 신간 도서 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 알라딘 API에서 베스트셀러를 가져옵니다.
     */
    @GetMapping("/bestseller")
    public ResponseEntity<ApiResponse<List<Book>>> getBestSellers() {
        log.info("알라딘 API 베스트셀러 테스트");
        try {
            List<Book> books = aladinApiClient.getBestSellers();
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("알라딘 API에서 베스트셀러 목록을 가져왔습니다. 총 %d개", books.size()),
                    books));
        } catch (Exception e) {
            log.error("알라딘 API 베스트셀러 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("알라딘 API 베스트셀러 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * ISBN으로 도서 상세 정보를 조회합니다.
     */
    @GetMapping("/book/{isbn}")
    public ResponseEntity<ApiResponse<Book>> getBookByIsbn(@PathVariable String isbn) {
        log.info("알라딘 API ISBN 상세 조회 테스트: {}", isbn);
        try {
            Book book = aladinApiClient.getBookDetailByIsbn(isbn);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("알라딘 API에서 ISBN '%s'에 해당하는 도서 정보를 가져왔습니다.", isbn),
                    book));
        } catch (Exception e) {
            log.error("알라딘 API ISBN 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("알라딘 API ISBN 상세 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 