package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.application.dto.book.BookDto;

/**
 * 책 수정을 위한 입력 포트
 */
public interface UpdateBookUseCase {
    /**
     * 책 정보를 수정합니다.
     *
     * @param command 책 수정 정보
     * @return 수정된 책의 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값이거나 책이 존재하지 않는 경우
     */
    BookDto.Responses.BookDetail updateBook(BookDto.Commands.Update command);
}