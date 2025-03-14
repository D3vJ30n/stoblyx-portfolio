package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * 책 JPA 리포지토리
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn AND b.isDeleted = false")
    Optional<Book> findByIsbn(@Param("isbn") String isbn);

    @Query("SELECT b FROM Book b WHERE b.isDeleted = false " +
            "AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))")
    Page<Book> findByTitleOrAuthorContainingIgnoreCase(@Param("searchKeyword") String searchKeyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.isDeleted = false")
    @NonNull Page<Book> findAll(@NonNull Pageable pageable);

    /**
     * N+1 문제 해결을 위해 EntityGraph 사용
     * 책 조회 시 장르 정보를 함께 로딩
     */
    @EntityGraph(attributePaths = {"genres"})
    Optional<Book> findByIdAndIsDeletedFalse(Long id);
    
    @EntityGraph(attributePaths = {"genres"})
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false")
    @NonNull Page<Book> findByIsDeletedFalse(@NonNull Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.genres g WHERE " +
           "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR g = :category) " +
           "AND b.isDeleted = false")
    @NonNull Page<Book> findByKeywordAndCategory(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @NonNull Pageable pageable
    );

    @Query("SELECT DISTINCT b FROM Book b WHERE b.isDeleted = false AND :genre MEMBER OF b.genres")
    @NonNull Page<Book> findByGenresContainingAndIsDeletedFalse(@Param("genre") String genre, @NonNull Pageable pageable);

    boolean existsByIsbn(String isbn);
    
    /**
     * 키셋 기반 페이징을 사용한 책 검색 (ID 기준)
     * 대용량 데이터에서 성능 향상
     */
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.genres g " +
           "WHERE b.id > :lastId " +
           "AND (:keyword IS NULL OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR g = :category) " +
           "AND b.isDeleted = false " +
           "ORDER BY b.id ASC")
    List<Book> findByKeywordAndCategoryWithKeyset(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("lastId") Long lastId,
            Pageable pageable);
    
    /**
     * 키셋 기반 페이징을 사용한 책 검색 (인기도 기준)
     * 대용량 데이터에서 성능 향상
     */
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.genres g " +
           "WHERE (b.popularity < :lastPopularity OR (b.popularity = :lastPopularity AND b.id > :lastId)) " +
           "AND (:keyword IS NULL OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR g = :category) " +
           "AND b.isDeleted = false " +
           "ORDER BY b.popularity DESC, b.id ASC")
    List<Book> findByKeywordAndCategoryWithKeysetByPopularity(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("lastPopularity") Integer lastPopularity,
            @Param("lastId") Long lastId,
            Pageable pageable);
            
    /**
     * 인기도 순으로 도서 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 인기도 순으로 정렬된 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false ORDER BY b.popularity DESC")
    @NonNull
    Page<Book> findMostPopularBooks(@NonNull Pageable pageable);
    
    /**
     * 특정 장르의 도서 목록 조회
     *
     * @param genre 장르
     * @param pageable 페이징 정보
     * @return 특정 장르의 도서 목록
     */
    @Query("SELECT DISTINCT b FROM Book b WHERE b.isDeleted = false AND :genre MEMBER OF b.genres ORDER BY b.popularity DESC")
    @NonNull
    Page<Book> findByGenre(@Param("genre") String genre, @NonNull Pageable pageable);
    
    /**
     * 특정 저자의 다른 도서 목록 조회
     *
     * @param author 저자
     * @param excludeBookId 제외할 도서 ID
     * @param pageable 페이징 정보
     * @return 특정 저자의 다른 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false AND b.author = :author AND b.id != :excludeBookId")
    @NonNull
    Page<Book> findByAuthorAndIdNot(@Param("author") String author, @Param("excludeBookId") Long excludeBookId, @NonNull Pageable pageable);
    
    /**
     * 인기도 순으로 도서 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 인기도 순으로 정렬된 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false ORDER BY b.popularity DESC")
    @NonNull
    Page<Book> findByIsDeletedFalseOrderByPopularityDesc(@NonNull Pageable pageable);
    
    /**
     * 최신 생성일 순으로 도서 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 최신 생성일 순으로 정렬된 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false ORDER BY b.createdAt DESC")
    @NonNull
    Page<Book> findByIsDeletedFalseOrderByCreatedAtDesc(@NonNull Pageable pageable);
    
    /**
     * 삭제되지 않은 모든 도서 목록 조회
     *
     * @return 모든 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false")
    @NonNull
    List<Book> findByIsDeletedFalse();
    
    /**
     * 장르 정보를 포함한 모든 도서 목록 조회
     *
     * @return 장르 정보를 포함한 모든 도서 목록
     */
    @EntityGraph(attributePaths = {"genres"})
    @Query("SELECT b FROM Book b WHERE b.isDeleted = false")
    @NonNull
    List<Book> findAllWithGenres();

    /**
     * 삭제되지 않은 책의 개수를 조회합니다.
     * 
     * @return 삭제되지 않은 책의 개수
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.isDeleted = false")
    long countByIsDeletedFalse();
}