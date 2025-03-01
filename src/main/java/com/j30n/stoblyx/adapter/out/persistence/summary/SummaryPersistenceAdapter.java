package com.j30n.stoblyx.adapter.out.persistence.summary;

import com.j30n.stoblyx.application.port.out.summary.SummaryPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Summary;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SummaryPersistenceAdapter implements SummaryPort {
    private final SummaryRepository summaryRepository;
    private final BookRepository bookRepository;

    @Override
    public Summary save(Summary summary) {
        return summaryRepository.save(summary);
    }

    @Override
    public Optional<Summary> findById(Long summaryId) {
        return summaryRepository.findById(summaryId);
    }

    @Override
    public Page<Summary> findByBook(Book book, Pageable pageable) {
        return summaryRepository.findByBookIdAndDeletedFalse(book.getId(), pageable);
    }

    @Override
    public void delete(Summary summary) {
        summaryRepository.delete(summary);
    }

    @Override
    public Optional<Book> findBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }
}
