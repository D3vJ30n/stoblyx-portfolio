package com.j30n.stoblyx.adapter.out.persistence.search;

import com.j30n.stoblyx.application.port.out.search.SearchPort;
import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.Search;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.SearchRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class SearchPersistenceAdapter implements SearchPort {
    private final QuoteRepository quoteRepository;
    private final BookRepository bookRepository;
    private final SearchRepository searchRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Quote> findQuotesByKeywordAndCategory(String searchTerm, String searchType, Pageable pageable) {
        return quoteRepository.findByKeywordAndCategory(searchTerm, searchType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findBooksByKeywordAndCategory(String searchTerm, String searchType, Pageable pageable) {
        return bookRepository.findByKeywordAndCategory(searchTerm, searchType, pageable);
    }
    
    @Override
    public Search saveSearch(String searchTerm, String searchType, Long userId, Integer searchCount) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElse(null);
        }
        
        Search search = Search.builder()
                .searchTerm(searchTerm)
                .searchType(searchType)
                .user(user)
                .searchCount(searchCount)
                .build();
        
        return searchRepository.save(search);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Search> findSearchesByUserId(Long userId, Pageable pageable) {
        return searchRepository.findByUserId(userId, pageable);
    }
    
    @Override
    public void deleteSearch(Long searchId) {
        searchRepository.deleteById(searchId);
    }
    
    @Override
    public void deleteAllSearchesByUserId(Long userId) {
        searchRepository.deleteByUserId(userId);
    }
}
