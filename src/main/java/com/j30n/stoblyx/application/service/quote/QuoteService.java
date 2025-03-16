package com.j30n.stoblyx.application.service.quote;

import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteResponse;
import com.j30n.stoblyx.adapter.in.web.dto.quote.QuoteUpdateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.quote.SavedQuoteRequest;
import com.j30n.stoblyx.application.port.in.quote.QuoteUseCase;
import com.j30n.stoblyx.application.port.out.book.BookPort;
import com.j30n.stoblyx.application.port.out.quote.QuotePort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.LikeRepository;
import com.j30n.stoblyx.domain.repository.SavedQuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
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

import java.util.Optional;

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
    private final BookPort bookPort;

    @PostConstruct
    public void init() {
        log.info("QuoteService 초기화됨");
        log.info("QuotePort 주입 상태: {}, 구현 클래스: {}",
            (quotePort != null ? "성공" : "실패"),
            (quotePort != null ? quotePort.getClass().getName() : "null"));

        log.info("UserRepository 주입 상태: {}, 구현 클래스: {}",
            (userRepository != null ? "성공" : "실패"),
            (userRepository != null ? userRepository.getClass().getName() : "null"));

        log.info("BookRepository 주입 상태: {}, 구현 클래스: {}",
            (bookRepository != null ? "성공" : "실패"),
            (bookRepository != null ? bookRepository.getClass().getName() : "null"));

        log.info("LikeRepository 주입 상태: {}, 구현 클래스: {}",
            (likeRepository != null ? "성공" : "실패"),
            (likeRepository != null ? likeRepository.getClass().getName() : "null"));

        log.info("SavedQuoteRepository 주입 상태: {}, 구현 클래스: {}",
            (savedQuoteRepository != null ? "성공" : "실패"),
            (savedQuoteRepository != null ? savedQuoteRepository.getClass().getName() : "null"));

        log.info("BookPort 주입 상태: {}, 구현 클래스: {}",
            (bookPort != null ? "성공" : "실패"),
            (bookPort != null ? bookPort.getClass().getName() : "null"));

        // 테스트용 책 존재 여부 확인
        try {
            Optional<Book> testBook = bookRepository.findByIdAndIsDeletedFalse(1L);
            if (testBook.isPresent()) {
                log.info("테스트용 책(ID: 1) 확인 성공 - 제목: {}, 저자: {}",
                    testBook.get().getTitle(), testBook.get().getAuthor());
            } else {
                log.warn("테스트용 책(ID: 1)이 BookRepository에 존재하지 않습니다.");
            }

            Optional<Book> testBookViaPort = bookPort.findById(1L);
            if (testBookViaPort.isPresent()) {
                log.info("BookPort를 통한 테스트용 책(ID: 1) 확인 성공 - 제목: {}, 저자: {}",
                    testBookViaPort.get().getTitle(), testBookViaPort.get().getAuthor());
            } else {
                log.warn("테스트용 책(ID: 1)이 BookPort를 통해 조회되지 않습니다.");
            }
        } catch (Exception e) {
            log.error("테스트용 책 확인 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "quotesCache", key = "'user_' + #userId + '_page_*'", allEntries = true)
    public QuoteResponse createQuote(Long userId, QuoteCreateRequest request) {
        log.debug("인용구 생성 시작: userId={}, bookId={}", userId, request.bookId());
        log.debug("요청 데이터: bookId={}, content={}, memo={}, page={}",
            request.bookId(),
            request.content(),
            request.memo(),
            request.page());

        User user = findUserById(userId);
        log.debug("사용자 조회 성공: id={}, username={}", user.getId(), user.getUsername());

        try {
            log.debug("책 조회 시작: bookId={}", request.bookId());
            Book book = findBookById(request.bookId());
            log.debug("책 조회 성공: id={}, title={}, author={}, isbn={}",
                book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn());

            log.debug("인용구 객체 생성 시작");
            Quote quote = Quote.builder()
                .user(user)
                .book(book)
                .content(request.content())
                .memo(request.memo())
                .page(request.page())
                .build();

            log.debug("인용구 객체 생성 완료: user={}, book={}, content={}",
                quote.getUser().getUsername(),
                quote.getBook().getTitle(),
                quote.getContent());

            log.debug("인용구 저장 시작");
            Quote savedQuote = quotePort.save(quote);
            log.debug("인용구 저장 성공: id={}", savedQuote.getId());

            QuoteResponse response = QuoteResponse.from(savedQuote, false, false);
            log.debug("인용구 응답 생성 완료: id={}", response.id());

            return response;
        } catch (Exception e) {
            log.error("인용구 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
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

    /**
     * 인용구에 좋아요를 표시합니다.
     *
     * @param userId  사용자 ID
     * @param quoteId 인용구 ID
     */
    @Transactional
    public void likeQuote(Long userId, Long quoteId) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));

        // 이미 좋아요한 경우 예외 처리
        if (likeRepository.findByUserIdAndQuoteId(userId, quoteId).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요한 인용구입니다.");
        }

        User user = findUserById(userId);
        quotePort.likeQuote(user, quote);
    }

    /**
     * 인용구 좋아요를 취소합니다.
     *
     * @param userId  사용자 ID
     * @param quoteId 인용구 ID
     */
    @Transactional
    public void unlikeQuote(Long userId, Long quoteId) {
        Quote quote = quotePort.findQuoteById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MESSAGE + quoteId));

        // 좋아요하지 않은 경우 예외 처리
        if (likeRepository.findByUserIdAndQuoteId(userId, quoteId).isEmpty()) {
            throw new IllegalArgumentException("좋아요하지 않은 인용구입니다.");
        }

        quotePort.unlikeQuote(userId, quoteId);
    }

    private User findUserById(Long userId) {
        log.debug("사용자 조회 시도 (ID: {})", userId);
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            log.debug("사용자 조회 성공 (ID: {}, 사용자명: {})", userId, user.getUsername());
            return user;
        } catch (Exception e) {
            log.error("사용자 조회 실패 (ID: {}): {}", userId, e.getMessage(), e);
            throw new EntityNotFoundException("인용구 생성 실패: 사용자 ID " + userId + "를 찾을 수 없습니다. 오류: " + e.getMessage());
        }
    }

    private Book findBookById(Long bookId) {
        log.debug("BookPort를 사용하여 책 조회 시도 (ID: {})", bookId);
        try {
            Optional<Book> bookFromPort = bookPort.findById(bookId);

            if (bookFromPort.isEmpty()) {
                log.debug("BookPort로 책을 찾지 못했습니다. BookRepository를 직접 사용하여 조회합니다.");
                // BookRepository를 직접 사용하여 조회
                Optional<Book> bookFromRepository = bookRepository.findByIdAndIsDeletedFalse(bookId);

                if (bookFromRepository.isPresent()) {
                    log.debug("BookRepository로 책을 찾았습니다: {}", bookFromRepository.get().getTitle());
                    return bookFromRepository.get();
                } else {
                    log.error("책을 찾지 못했습니다. BookRepository에서도 ID {}인 책이 없습니다.", bookId);
                    throw new EntityNotFoundException("인용구 생성 실패: ID가 " + bookId + "인 책을 찾을 수 없습니다. 해당 책이 존재하는지 확인하세요.");
                }
            }

            log.debug("BookPort로 책을 찾았습니다: {}", bookFromPort.get().getTitle());
            return bookFromPort.get();
        } catch (EntityNotFoundException e) {
            // 이미 적절한 메시지가 있는 경우 그대로 전달
            throw e;
        } catch (Exception e) {
            log.error("책 조회 중 예상치 못한 오류 발생 (ID: {}): {}", bookId, e.getMessage(), e);
            throw new EntityNotFoundException("인용구 생성 실패: 책 ID " + bookId + "를 조회하는 중 오류가 발생했습니다. 오류: " + e.getMessage());
        }
    }

    private void validateQuoteOwner(Long quoteId, Long userId) {
        if (!quotePort.existsByIdAndUserId(quoteId, userId)) {
            throw new IllegalArgumentException("You don't have permission to modify this quote");
        }
    }
}