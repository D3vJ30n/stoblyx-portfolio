package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn AND b.deleted = false")
    Optional<Book> findByIsbn(@Param("isbn") String isbn);

    @Query("SELECT b FROM Book b WHERE b.deleted = false " +
            "AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))")
    Page<Book> findByTitleOrAuthorContainingIgnoreCase(@Param("searchKeyword") String searchKeyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.deleted = false")
    Page<Book> findAll(Pageable pageable);

    Optional<Book> findByIdAndDeletedFalse(Long id);
    Page<Book> findByDeletedFalse(Pageable pageable);
} 