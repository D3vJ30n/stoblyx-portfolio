package com.j30n.stoblyx.application.usecase.book.port;

import com.j30n.stoblyx.application.dto.book.UpdateBookCommand;

public interface UpdateBookUseCase {
    void updateBook(UpdateBookCommand command);
}