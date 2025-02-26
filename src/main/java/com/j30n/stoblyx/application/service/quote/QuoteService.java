package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.SavedQuoteRequest;
import com.j30n.stoblyx.application.port.in.quote.QuoteUseCase;
import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.LikeRepository;
import com.j30n.stoblyx.domain.repository.SavedQuotesRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteService implements QuoteUseCase {

    private final QuotePort quotePort;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final SavedQuotesRepository savedQuotesRepository;

    @Override
    @Transactional
    public QuoteResponse createQuote(Long userId, QuoteCreateRequest request) {
        User user = findUserById(userId);
        Book book = findBookById(request.bookId());

        Quote quote = Quote.builder()
            .content(request.content())
            .page(request.page())
            .memo(request.memo())
            .user(user)
            .book(book)
            .build();

        Quote savedQuote = quotePort.save(quote);
        return QuoteResponse.from(savedQuote, false, false);
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteResponse getQuote(Long quoteId, Long userId) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("Quote not found with id: " + quoteId));
        boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        boolean isSaved = savedQuotesRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        return QuoteResponse.from(quote, isLiked, isSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteResponse> getQuotes(Long userId, Pageable pageable) {
        return quotePort.findByUserId(userId, pageable)
            .map(quote -> {
                boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quote.getId()).isPresent();
                boolean isSaved = savedQuotesRepository.findByUserIdAndQuoteId(userId, quote.getId()).isPresent();
                return QuoteResponse.from(quote, isLiked, isSaved);
            });
    }

    @Override
    @Transactional
    public QuoteResponse updateQuote(Long quoteId, Long userId, QuoteUpdateRequest request) {
        validateQuoteOwner(quoteId, userId);
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("Quote not found with id: " + quoteId));
        quote.update(
            request.content(),
            request.memo(),
            request.page()
        );
        Quote updatedQuote = quotePort.save(quote);
        boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        boolean isSaved = savedQuotesRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        return QuoteResponse.from(updatedQuote, isLiked, isSaved);
    }

    @Override
    @Transactional
    public void deleteQuote(Long quoteId, Long userId) {
        validateQuoteOwner(quoteId, userId);
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("Quote not found with id: " + quoteId));
        quotePort.delete(quote);
    }

    @Override
    @Transactional
    public QuoteResponse saveQuote(Long userId, Long quoteId, SavedQuoteRequest request) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("Quote not found with id: " + quoteId));
        quotePort.saveQuoteToUser(userId, quoteId, request.note());
        boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        return QuoteResponse.from(quote, isLiked, true);
    }

    @Override
    @Transactional
    public void unsaveQuote(Long userId, Long quoteId) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("Quote not found with id: " + quoteId));
        quotePort.unsaveQuoteFromUser(userId, quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteResponse> getSavedQuotes(Long userId, Pageable pageable) {
        return quotePort.findSavedQuotesByUserId(userId, pageable)
            .map(quote -> {
                boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quote.getId()).isPresent();
                return QuoteResponse.from(quote, isLiked, true);
            });
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
    }

    private void validateQuoteOwner(Long quoteId, Long userId) {
        if (!quotePort.existsByIdAndUserId(quoteId, userId)) {
            throw new IllegalStateException("You don't have permission to modify this quote");
        }
    }
}