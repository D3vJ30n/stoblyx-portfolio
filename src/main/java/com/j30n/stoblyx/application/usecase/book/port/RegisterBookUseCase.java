package com.j30n.stoblyx.application.usecase.book.port;

import com.j30n.stoblyx.application.dto.book.RegisterBookCommand;
import com.j30n.stoblyx.domain.model.book.BookId;

public interface RegisterBookUseCase {
    BookId registerBook(RegisterBookCommand command);
}