package com.j30n.stoblyx.adapter.out.persistence.quote;

import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.SavedQuote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.SavedQuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuotePersistenceAdapter implements QuotePort {

    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final SavedQuoteRepository savedQuoteRepository;

    @Override
    public Quote save(Quote quote) {
        return quoteRepository.save(quote);
    }

    @Override
    public Optional<Quote> findQuoteById(Long id) {
        return quoteRepository.findById(id);
    }

    @Override
    public Page<Quote> findByUserId(Long userId, Pageable pageable) {
        return quoteRepository.findByUserId(userId, pageable);
    }

    @Override
    public void delete(Quote quote) {
        quote.delete();
        quoteRepository.save(quote);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return quoteRepository.existsByIdAndUserId(id, userId);
    }

    @Override
    public void saveQuoteToUser(Long userId, Long quoteId, String note) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id: " + userId));
        Quote quote = findQuoteById(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인용구입니다. id: " + quoteId));

        savedQuoteRepository.findByUserIdAndQuoteId(userId, quoteId)
            .ifPresentOrElse(
                savedQuote -> {
                    if (savedQuote.isDeleted()) {
                        savedQuote.restore();
                        savedQuote.updateNote(note);
                        savedQuoteRepository.save(savedQuote);
                    }
                },
                () -> {
                    SavedQuote savedQuote = SavedQuote.builder()
                        .user(user)
                        .quote(quote)
                        .note(note)
                        .build();
                    savedQuoteRepository.save(savedQuote);
                }
            );
    }

    @Override
    public void unsaveQuoteFromUser(Long userId, Long quoteId) {
        savedQuoteRepository.findByUserIdAndQuoteId(userId, quoteId)
            .ifPresent(savedQuote -> {
                savedQuote.delete();
                savedQuoteRepository.save(savedQuote);
            });
    }

    @Override
    public Page<Quote> findSavedQuotesByUserId(Long userId, Pageable pageable) {
        return savedQuoteRepository.findByUserId(userId, pageable)
            .map(SavedQuote::getQuote);
    }
}
