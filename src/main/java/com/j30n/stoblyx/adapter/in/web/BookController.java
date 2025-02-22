package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.application.dto.book.BookDto;
import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.domain.port.in.book.DeleteBookUseCase;
import com.j30n.stoblyx.domain.port.in.book.FindBookUseCase;
import com.j30n.stoblyx.domain.port.in.book.RegisterBookUseCase;
import com.j30n.stoblyx.domain.port.in.book.UpdateBookUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final RegisterBookUseCase registerBookUseCase;
    private final UpdateBookUseCase updateBookUseCase;
    private final FindBookUseCase findBookUseCase;
    private final DeleteBookUseCase deleteBookUseCase;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookDto.Responses.BookDetail>> registerBook(
        @Valid @RequestBody BookDto.Commands.Create command
    ) {
        try {
            var response = registerBookUseCase.registerBook(command);
            return ResponseEntity.ok(ApiResponse.success("책이 성공적으로 등록되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto.Responses.BookDetail>> findBook(@PathVariable Long id) {
        try {
            var query = new BookDto.Queries.FindById(id);
            var response = findBookUseCase.findById(query);
            return ResponseEntity.ok(ApiResponse.success("책 조회에 성공했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDto.Responses.BookSummary>>> findAllBooks() {
        try {
            var response = findBookUseCase.findAll();
            return ResponseEntity.ok(ApiResponse.success("책 목록 조회에 성공했습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookDto.Responses.BookDetail>> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookDto.Commands.Update command
    ) {
        try {
            var response = updateBookUseCase.updateBook(command);
            return ResponseEntity.ok(ApiResponse.success("책이 성공적으로 수정되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
        @PathVariable Long id,
        @Valid @RequestBody BookDto.Commands.Delete command
    ) {
        try {
            deleteBookUseCase.deleteBook(command);
            return ResponseEntity.ok(ApiResponse.success("책이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}