package com.j30n.stoblyx.application.port.out.summary;

import com.j30n.stoblyx.domain.model.Book;
import com.j30n.stoblyx.domain.model.Summary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SummaryPort {
    /**
     * 요약을 저장합니다.
     *
     * @param summary 저장할 요약
     * @return 저장된 요약
     */
    Summary save(Summary summary);

    /**
     * ID로 요약을 조회합니다.
     *
     * @param summaryId 요약 ID
     * @return 요약 Optional
     */
    Optional<Summary> findById(Long summaryId);

    /**
     * 특정 책의 모든 요약을 조회합니다.
     *
     * @param book 책
     * @param pageable 페이징 정보
     * @return 요약 목록
     */
    Page<Summary> findByBook(Book book, Pageable pageable);

    /**
     * 요약을 삭제합니다.
     *
     * @param summary 삭제할 요약
     */
    void delete(Summary summary);

    /**
     * ID로 책을 조회합니다.
     *
     * @param bookId 책 ID
     * @return 책 Optional
     */
    Optional<Book> findBookById(Long bookId);
}
