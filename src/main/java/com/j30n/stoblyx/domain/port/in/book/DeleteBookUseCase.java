package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.application.dto.book.BookDto;

/**
 * 책 삭제를 위한 입력 포트
 */
public interface DeleteBookUseCase {
    /**
     * 책을 삭제합니다.
     *
     * @param command 책 삭제 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 책이 존재하지 않는 경우
     */
    void deleteBook(BookDto.Commands.Delete command);
}