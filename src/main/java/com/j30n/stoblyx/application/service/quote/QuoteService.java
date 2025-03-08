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
import com.j30n.stoblyx.domain.repository.SavedQuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteService implements QuoteUseCase {

    private static final String QUOTE_NOT_FOUND_MESSAGE = "Quote not found with id: ";

    private final QuotePort quotePort;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final SavedQuoteRepository savedQuoteRepository;

    @Override
    @Transactional
    @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
    public QuoteResponse createQuote(Long userId, QuoteCreateRequest request) {
        User user = findUserById(userId);
        Book book = findBookById(request.bookId());

        Quote quote = Quote.builder()
                .user(user)
                .book(book)
                .content(request.content())
                .memo(request.memo())
                .page(request.page())
                .build();

        Quote savedQuote = quotePort.save(quote);
        return QuoteResponse.from(savedQuote, false, false);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "quotesCache", key = "#quoteId", unless = "#result == null")
    public QuoteResponse getQuote(Long quoteId, Long userId) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));
        boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        boolean isSaved = savedQuoteRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        return QuoteResponse.from(quote, isLiked, isSaved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "quotesCache", key = "'user_' + #userId + '_page_' + #pageable.pageNumber", unless = "#result.isEmpty()")
    public Page<QuoteResponse> getQuotes(Long userId, Pageable pageable) {
        return quotePort.findByUserId(userId, pageable)
            .map(quote -> {
                boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quote.getId()).isPresent();
                boolean isSaved = savedQuoteRepository.findByUserIdAndQuoteId(userId, quote.getId()).isPresent();
                return QuoteResponse.from(quote, isLiked, isSaved);
            });
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "quotesCache", key = "#quoteId"),
        @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
    })
    public QuoteResponse updateQuote(Long quoteId, Long userId, QuoteUpdateRequest request) {
        validateQuoteOwner(quoteId, userId);

        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));

        quote.update(request.content(), request.memo(), request.page());
        Quote updatedQuote = quotePort.save(quote);

        boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        boolean isSaved = savedQuoteRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        return QuoteResponse.from(updatedQuote, isLiked, isSaved);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "quotesCache", key = "#quoteId"),
        @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
    })
    public void deleteQuote(Long quoteId, Long userId) {
        validateQuoteOwner(quoteId, userId);
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));
        quotePort.delete(quote);
    }

    @Override
    @Transactional
    @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
    public QuoteResponse saveQuote(Long userId, Long quoteId, SavedQuoteRequest request) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));
        
        quotePort.saveQuoteToUser(userId, quoteId, request.note());
        
        boolean isLiked = likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent();
        return QuoteResponse.from(quote, isLiked, true);
    }

    @Override
    @Transactional
    @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
    public void unsaveQuote(Long userId, Long quoteId) {
        quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));
        
        quotePort.unsaveQuoteFromUser(userId, quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "quotesCache", key = "'saved_' + #userId + '_page_' + #pageable.pageNumber", unless = "#result.isEmpty()")
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
            throw new IllegalArgumentException("You don't have permission to modify this quote");
        }
    }
}