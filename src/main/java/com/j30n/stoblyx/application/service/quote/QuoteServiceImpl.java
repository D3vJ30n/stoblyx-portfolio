package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.adapter.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public QuoteResponse createQuote(Long userId, QuoteCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + request.bookId()));

        Quote quote = Quote.builder()
                .content(request.content())
                .page(request.page())
                .chapter(request.chapter())
                .user(user)
                .book(book)
                .build();

        return QuoteResponse.from(quoteRepository.save(quote));
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteResponse getQuote(Long id) {
        return quoteRepository.findById(id)
                .map(QuoteResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("문구를 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteResponse> getQuotes(Long userId, Long bookId, Pageable pageable) {
        if (userId != null && bookId != null) {
            return quoteRepository.findByUserIdAndBookId(userId, bookId, pageable)
                    .map(QuoteResponse::from);
        } else if (userId != null) {
            return quoteRepository.findByUserId(userId, pageable)
                    .map(QuoteResponse::from);
        } else if (bookId != null) {
            return quoteRepository.findByBookId(bookId, pageable)
                    .map(QuoteResponse::from);
        }
        return quoteRepository.findAll(pageable)
                .map(QuoteResponse::from);
    }

    @Override
    @Transactional
    public QuoteResponse updateQuote(Long userId, Long quoteId, QuoteCreateRequest request) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("문구를 찾을 수 없습니다: " + quoteId));

        if (!quote.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("문구를 수정할 권한이 없습니다.");
        }

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다: " + request.bookId()));

        quote.update(request.content(), request.page(), request.chapter());
        
        return QuoteResponse.from(quote);
    }

    @Override
    @Transactional
    public void deleteQuote(Long userId, Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("문구를 찾을 수 없습니다: " + quoteId));

        if (!quote.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("문구를 삭제할 권한이 없습니다.");
        }

        quote.delete();
    }
} 