package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.common.exception.book.BookNotFoundException;
import com.j30n.stoblyx.domain.model.book.BookId;

public interface DeleteBookUseCase {
    /**
     * 책을 삭제합니다.
     *
     * @param id 삭제할 책의 ID
     * @throws BookNotFoundException 해당 ID의 책을 찾을 수 없는 경우
     */
    void deleteBook(BookId id);
}