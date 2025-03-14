package com.j30n.stoblyx.adapter.out.persistence.quote;

import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.domain.model.Like;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.SavedQuote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.LikeRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.SavedQuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QuotePersistenceAdapter implements QuotePort {

    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final SavedQuoteRepository savedQuoteRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public Quote save(Quote quote) {
        try {
            log.debug("인용구 저장 시작: bookId={}, userId={}", 
                quote.getBook().getId(), quote.getUser().getId());
            
            // 참조 무결성 검사
            if (quote.getUser() == null || quote.getUser().getId() == null) {
                throw new IllegalArgumentException("인용구에 유효한 사용자 정보가 없습니다.");
            }
            
            if (quote.getBook() == null || quote.getBook().getId() == null) {
                throw new IllegalArgumentException("인용구에 유효한 책 정보가 없습니다.");
            }
            
            // 사용자 존재 여부 검사
            if (!userRepository.existsById(quote.getUser().getId())) {
                throw new EntityNotFoundException("인용구 저장 실패: 사용자 ID " + quote.getUser().getId() + "가 존재하지 않습니다.");
            }
            
            // 저장 실행
            Quote savedQuote = quoteRepository.save(quote);
            log.debug("인용구 저장 성공: id={}", savedQuote.getId());
            return savedQuote;
        } catch (Exception e) {
            log.error("인용구 저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
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

    @Override
    public void likeQuote(User user, Quote quote) {
        Like like = Like.builder()
            .user(user)
            .quote(quote)
            .build();
        likeRepository.save(like);
    }

    @Override
    public void unlikeQuote(Long userId, Long quoteId) {
        likeRepository.findByUserIdAndQuoteId(userId, quoteId)
            .ifPresent(likeRepository::delete);
    }
}
