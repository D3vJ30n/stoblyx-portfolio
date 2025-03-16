package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.ApiResponse;
import com.j30n.stoblyx.adapter.in.web.dto.book.BookResponse;
import com.j30n.stoblyx.application.service.book.AladinBookService;
import com.j30n.stoblyx.domain.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 알라딘 API를 통해 책 데이터를 가져오는 관리자용 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/admin/books/import")
@RequiredArgsConstructor
public class AladinBookImportController {

    private final AladinBookService aladinBookService;

    /**
     * 알라딘 API에서 신간 도서 목록을 가져옵니다.
     *
     * @param count 가져올 도서 수 (최대 50개)
     * @return 가져온 도서 목록
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public ResponseEntity<ApiResponse<List<BookResponse>>> importNewBooks(
        @RequestParam(defaultValue = "50") int count) {
        try {
            if (count > 50) {
                count = 50; // 최대 50개로 제한
            }

            List<Book> importedBooks = aladinBookService.fetchAndSaveNewBooks();
            List<BookResponse> bookResponses = importedBooks.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                String.format("알라딘 API에서 %d개의 신간 도서를 성공적으로 가져왔습니다.", bookResponses.size()),
                bookResponses
            ));
        } catch (Exception e) {
            log.error("알라딘 API에서 신간 도서를 가져오는 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                "ERROR",
                "알라딘 API에서 신간 도서를 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * 알라딘 API에서 베스트셀러 목록을 가져옵니다.
     *
     * @param count 가져올 도서 수 (최대 50개)
     * @return 가져온 도서 목록
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bestsellers")
    public ResponseEntity<ApiResponse<List<BookResponse>>> importBestSellers(
        @RequestParam(defaultValue = "50") int count) {
        try {
            if (count > 50) {
                count = 50; // 최대 50개로 제한
            }

            List<Book> importedBooks = aladinBookService.fetchAndSaveBestSellers();
            List<BookResponse> bookResponses = importedBooks.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                String.format("알라딘 API에서 %d개의 베스트셀러를 성공적으로 가져왔습니다.", bookResponses.size()),
                bookResponses
            ));
        } catch (Exception e) {
            log.error("알라딘 API에서 베스트셀러를 가져오는 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                "ERROR",
                "알라딘 API에서 베스트셀러를 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * 알라딘 API에서 키워드로 책을 검색하여 가져옵니다.
     *
     * @param keyword 검색 키워드
     * @param count   가져올 도서 수 (최대 50개)
     * @return 가져온 도서 목록
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<BookResponse>>> importBooksByKeyword(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "50") int count) {
        try {
            if (count > 50) {
                count = 50; // 최대 50개로 제한
            }

            List<Book> importedBooks = aladinBookService.fetchAndSaveBooksByKeyword(keyword);
            List<BookResponse> bookResponses = importedBooks.stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                String.format("알라딘 API에서 키워드 '%s'로 %d개의 도서를 성공적으로 가져왔습니다.",
                    keyword, bookResponses.size()),
                bookResponses
            ));
        } catch (Exception e) {
            log.error("알라딘 API에서 키워드로 도서를 가져오는 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(new ApiResponse<>(
                "ERROR",
                "알라딘 API에서 키워드로 도서를 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                null
            ));
        }
    }
} 