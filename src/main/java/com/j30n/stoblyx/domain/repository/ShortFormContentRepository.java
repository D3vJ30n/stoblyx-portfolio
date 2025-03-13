package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.enums.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShortFormContentRepository extends JpaRepository<ShortFormContent, Long> {

    Optional<ShortFormContent> findByIdAndIsDeletedFalse(Long id);

    Page<ShortFormContent> findByIsDeletedFalse(Pageable pageable);

    Page<ShortFormContent> findByQuote_User_IdAndIsDeletedFalse(Long userId, Pageable pageable);

    Page<ShortFormContent> findByBook_IdAndIsDeletedFalse(Long bookId, Pageable pageable);

    Page<ShortFormContent> findBySubtitlesContainingAndIsDeletedFalse(String keyword, Pageable pageable);

    @Query("SELECT c FROM ShortFormContent c WHERE c.isDeleted = false " +
           "ORDER BY c.viewCount DESC, c.likeCount DESC, c.shareCount DESC")
    Page<ShortFormContent> findTrendingContents(Pageable pageable);

    @Query("SELECT c FROM ShortFormContent c WHERE c.isDeleted = false " +
           "ORDER BY c.likeCount DESC, c.viewCount DESC, c.shareCount DESC")
    Page<ShortFormContent> findPopularContents(Pageable pageable);

    @Query("SELECT c FROM ShortFormContent c " +
           "WHERE c.isDeleted = false " +
           "AND (c.book.id IN (SELECT q.book.id FROM Quote q WHERE q.user.id = :userId) " +
           "OR c.quote.user.id = :userId) " +
           "ORDER BY c.createdAt DESC")
    Page<ShortFormContent> findRecommendedContents(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM ShortFormContent s " +
           "WHERE s.isDeleted = false " +
           "AND s.status = 'PUBLISHED' " +
           "AND s.quote.user.id != :userId " +
           "ORDER BY s.viewCount DESC, s.likeCount DESC")
    Page<ShortFormContent> findRecommendedContentsOriginal(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM ShortFormContent s " +
           "WHERE s.isDeleted = false " +
           "AND s.status = 'PUBLISHED' " +
           "AND (s.book.title LIKE CONCAT('%', :keyword, '%') " +
           "OR s.quote.content LIKE CONCAT('%', :keyword, '%') " +
           "OR s.subtitles LIKE CONCAT('%', :keyword, '%'))")
    Page<ShortFormContent> search(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByVideoUrl(String videoUrl);

    /**
     * 상태별로 삭제되지 않은 콘텐츠를 조회합니다.
     *
     * @param status 콘텐츠 상태
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    Page<ShortFormContent> findByStatusAndIsDeletedFalse(ContentStatus status, Pageable pageable);

    /**
     * 특정 날짜 이후에 생성된 콘텐츠 수를 조회합니다.
     */
    long countByCreatedAtAfter(LocalDateTime dateTime);
    
    /**
     * 특정 상태의 콘텐츠 수를 조회합니다.
     */
    long countByStatus(ContentStatus status);
    
    /**
     * 특정 유형의 콘텐츠 수를 조회합니다.
     */
    long countByContentType(ContentType contentType);

    // 특정 기간 내 생성된 콘텐츠 조회
    List<ShortFormContent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 특정 기간 내 생성된 콘텐츠 수 조회
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 특정 사용자의 특정 기간 내 생성된 콘텐츠 수 조회
    @Query("SELECT COUNT(c) FROM ShortFormContent c WHERE c.quote.user.id = :userId AND c.createdAt BETWEEN :start AND :end")
    long countByUserIdAndCreatedAtBetween(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * 특정 사용자가 작성한 콘텐츠 수를 조회합니다.
     * ShortFormContent 엔티티에 userId 필드가 없으므로 JPQL 쿼리로 구현합니다.
     */
    @Query("SELECT COUNT(c) FROM ShortFormContent c WHERE c.quote.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 특정 기간 내 일별 콘텐츠 생성 통계 조회
    @Query("SELECT FUNCTION('DATE', c.createdAt) as date, COUNT(c) as count FROM ShortFormContent c " +
           "WHERE c.createdAt BETWEEN :start AND :end GROUP BY FUNCTION('DATE', c.createdAt)")
    List<Object[]> countContentsByDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}