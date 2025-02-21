package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.application.dto.book.UpdateBookCommand;
import com.j30n.stoblyx.common.exception.book.BookNotFoundException;

public interface UpdateBookUseCase {
    /**
     * 기존 책 정보를 수정합니다.
     *
     * @param command 책 수정 명령
     * @throws IllegalArgumentException 유효하지 않은 입력값인 경우
     * @throws BookNotFoundException    해당 ID의 책을 찾을 수 없는 경우
     */
    void updateBook(UpdateBookCommand command);
}