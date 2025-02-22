package com.j30n.stoblyx.domain.port.in.book;

import com.j30n.stoblyx.application.dto.book.BookDto;

import java.util.List;

/**
 * 책 조회를 위한 입력 포트
 */
public interface FindBookUseCase {
    /**
     * ID로 책을 조회합니다.
     *
     * @param query 책 조회 쿼리
     * @return 조회된 책의 상세 정보
     * @throws IllegalArgumentException 책이 존재하지 않는 경우
     */
    BookDto.Responses.BookDetail findById(BookDto.Queries.FindById query);

    /**
     * 모든 책 목록을 조회합니다.
     *
     * @return 책 목록 요약 정보
     */
    List<BookDto.Responses.BookSummary> findAll();

    /**
     * 사용자가 등록한 책 목록을 조회합니다.
     *
     * @param query 사용자별 책 조회 쿼리
     * @return 책 목록 요약 정보
     */
    List<BookDto.Responses.BookSummary> findByUser(BookDto.Queries.FindByUser query);

    /**
     * ISBN으로 책을 조회합니다.
     *
     * @param query ISBN 조회 쿼리
     * @return 조회된 책의 상세 정보
     */
    BookDto.Responses.BookDetail findByIsbn(BookDto.Queries.FindByIsbn query);
}