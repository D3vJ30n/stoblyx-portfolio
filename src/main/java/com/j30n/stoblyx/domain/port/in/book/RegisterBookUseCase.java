package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.application.dto.book.BookResponse;
import com.j30n.stoblyx.application.dto.book.RegisterBookCommand;

public interface RegisterBookUseCase {
    /**
     * 새로운 책을 등록합니다.
     *
     * @param command 책 등록 명령
     * @return 등록된 책의 응답 정보
     * @throws IllegalArgumentException 유효하지 않은 입력값인 경우
     */
    BookResponse registerBook(RegisterBookCommand command);
}