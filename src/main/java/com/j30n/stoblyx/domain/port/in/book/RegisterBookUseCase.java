package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.application.dto.book.BookDto;

/**
 * 책 등록을 위한 입력 포트
 */
public interface RegisterBookUseCase {
    /**
     * 새로운 책을 등록합니다.
     *
     * @param command 책 등록 정보
     * @return 등록된 책의 상세 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값인 경우
     */
    BookDto.Responses.BookDetail registerBook(BookDto.Commands.Create command);
}