package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.ShortFormContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortFormContentRepository extends JpaRepository<ShortFormContent, Long> {

    Optional<ShortFormContent> findByIdAndDeletedFalse(Long id);

    Page<ShortFormContent> findByDeletedFalse(Pageable pageable);

    Page<ShortFormContent> findByQuote_User_IdAndDeletedFalse(Long userId, Pageable pageable);

    Page<ShortFormContent> findByBook_IdAndDeletedFalse(Long bookId, Pageable pageable);

    Page<ShortFormContent> findBySubtitlesContainingAndDeletedFalse(String keyword, Pageable pageable);

    @Query("SELECT c FROM ShortFormContent c WHERE c.deleted = false " +
           "ORDER BY c.viewCount DESC, c.likeCount DESC, c.shareCount DESC")
    Page<ShortFormContent> findTrendingContents(Pageable pageable);

    @Query("SELECT c FROM ShortFormContent c WHERE c.deleted = false " +
           "ORDER BY c.likeCount DESC, c.viewCount DESC, c.shareCount DESC")
    Page<ShortFormContent> findPopularContents(Pageable pageable);

    @Query("SELECT c FROM ShortFormContent c " +
           "WHERE c.deleted = false " +
           "AND (c.book.id IN (SELECT b.id FROM Book b WHERE b.user.id = :userId) " +
           "OR c.quote.user.id = :userId) " +
           "ORDER BY c.createdAt DESC")
    Page<ShortFormContent> findRecommendedContents(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM ShortFormContent s " +
           "WHERE s.deleted = false " +
           "AND s.status = 'PUBLISHED' " +
           "AND s.quote.user.id != :userId " +
           "ORDER BY s.viewCount DESC, s.likeCount DESC")
    Page<ShortFormContent> findRecommendedContentsOriginal(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM ShortFormContent s " +
           "WHERE s.deleted = false " +
           "AND s.status = 'PUBLISHED' " +
           "AND s.book.id = :bookId")
    Page<ShortFormContent> findByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Query("SELECT s FROM ShortFormContent s " +
           "WHERE s.deleted = false " +
           "AND s.status = 'PUBLISHED' " +
           "AND (s.book.title LIKE %:keyword% " +
           "OR s.quote.content LIKE %:keyword% " +
           "OR s.subtitles LIKE %:keyword%)")
    Page<ShortFormContent> search(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByVideoUrl(String videoUrl);
}