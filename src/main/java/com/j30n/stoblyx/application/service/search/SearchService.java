package com.j30n.stoblyx.application.service.search;

import com.j30n.stoblyx.adapter.in.web.dto.search.SearchRequest;
import com.j30n.stoblyx.adapter.in.web.dto.search.SearchResponse;
import com.j30n.stoblyx.application.port.in.search.SearchUseCase;
import com.j30n.stoblyx.application.port.out.search.SearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 검색 기능을 제공하는 서비스
 */
@Service
@RequiredArgsConstructor
public class SearchService implements SearchUseCase {
    private final SearchPort searchPort;

    @Override
    @Transactional(readOnly = true)
    public Page<SearchResponse> search(SearchRequest request, Pageable pageable) {
        return switch (request.type()) {
            case QUOTE -> searchQuotes(request.keyword(), request.category(), pageable);
            case BOOK -> searchBooks(request.keyword(), request.category(), pageable);
            case ALL -> searchAll(request.keyword(), pageable);
        };
    }

    private Page<SearchResponse> searchQuotes(String keyword, String category, Pageable pageable) {
        return searchPort.findQuotesByKeywordAndCategory(keyword, category, pageable)
            .map(SearchResponse::fromQuote);
    }

    private Page<SearchResponse> searchBooks(String keyword, String category, Pageable pageable) {
        return searchPort.findBooksByKeywordAndCategory(keyword, category, pageable)
            .map(SearchResponse::fromBook);
    }

    private Page<SearchResponse> searchAll(String keyword, Pageable pageable) {
        // 문구와 책을 모두 검색하고 결과를 통합
        Page<SearchResponse> quotes = searchQuotes(keyword, null, pageable);
        Page<SearchResponse> books = searchBooks(keyword, null, pageable);

        List<SearchResponse> combined = new ArrayList<>();
        combined.addAll(quotes.getContent());
        combined.addAll(books.getContent());

        // 생성일자 기준으로 정렬
        combined.sort(Comparator.comparing(SearchResponse::getCreatedAt).reversed());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combined.size());

        return new PageImpl<>(
            combined.subList(start, end),
            pageable,
            combined.size()
        );
    }
}
