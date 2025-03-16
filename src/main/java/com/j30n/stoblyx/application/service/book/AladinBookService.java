package com.j30n.stoblyx.application.service.book;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.repository.BookRepository;
import com.j30n.stoblyx.infrastructure.external.AladinApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 알라딘 API를 사용하여 책 정보를 가져오는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AladinBookService {

    private final AladinApiClient aladinApiClient;
    private final BookRepository bookRepository;

    /**
     * 알라딘 API에서 신간 도서 목록을 가져와 데이터베이스에 저장합니다.
     * 최대 200개까지 가져올 수 있습니다.
     * @return 가져온 책 목록
     */
    @Transactional
    public List<Book> fetchAndSaveNewBooks() {
        List<Book> newBooks = aladinApiClient.getNewBooks();
        log.info("알라딘 API에서 {}개의 신간 도서를 가져왔습니다.", newBooks.size());
        return bookRepository.saveAll(newBooks);
    }

    /**
     * 알라딘 API에서 베스트셀러 목록을 가져와 데이터베이스에 저장합니다.
     * 최대 200개까지 가져올 수 있습니다.
     * @return 가져온 책 목록
     */
    @Transactional
    public List<Book> fetchAndSaveBestSellers() {
        List<Book> bestSellers = aladinApiClient.getBestSellers();
        log.info("알라딘 API에서 {}개의 베스트셀러를 가져왔습니다.", bestSellers.size());
        return bookRepository.saveAll(bestSellers);
    }

    /**
     * 알라딘 API에서 키워드로 책을 검색하여 데이터베이스에 저장합니다.
     * 최대 200개까지 가져올 수 있습니다.
     * @param keyword 검색 키워드
     * @return 가져온 책 목록
     */
    @Transactional
    public List<Book> fetchAndSaveBooksByKeyword(String keyword) {
        List<Book> books = aladinApiClient.searchBooks(keyword);
        log.info("알라딘 API에서 키워드 '{}'로 {}개의 책을 검색했습니다.", keyword, books.size());
        return bookRepository.saveAll(books);
    }
    
    /**
     * ISBN으로 책 상세 정보를 조회하여 데이터베이스에 저장합니다.
     * @param isbn13 ISBN13 코드
     * @return 저장된 책 정보
     */
    @Transactional
    public Book fetchAndSaveBookByIsbn(String isbn13) {
        Book book = aladinApiClient.getBookDetailByIsbn(isbn13);
        if (book != null) {
            log.info("알라딘 API에서 ISBN '{}' 책 정보를 가져왔습니다: {}", isbn13, book.getTitle());
            return bookRepository.save(book);
        } else {
            log.warn("알라딘 API에서 ISBN '{}'에 해당하는 책을 찾을 수 없습니다.", isbn13);
            return null;
        }
    }
} 