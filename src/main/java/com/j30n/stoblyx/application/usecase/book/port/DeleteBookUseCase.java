package com.j30n.stoblyx.application.usecase.book.port;

import com.j30n.stoblyx.domain.model.book.BookId;

public interface DeleteBookUseCase {
    void deleteBook(BookId id);
}