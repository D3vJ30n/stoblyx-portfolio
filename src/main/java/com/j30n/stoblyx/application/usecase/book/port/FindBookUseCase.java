package com.j30n.stoblyx.application.usecase.book.port;

import com.j30n.stoblyx.application.dto.book.BookResponse;
import com.j30n.stoblyx.domain.model.book.BookId;

import java.util.List;
import java.util.Optional;

public interface FindBookUseCase {
    Optional<BookResponse> findById(BookId id);

    List<BookResponse> findAll();
}