package com.j30n.stoblyx.domain.repository;

import com.j30n.stoblyx.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 게시물 JPA 리포지토리
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.isDeleted = false")
    Page<Post> findByAuthorId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Post p WHERE p.id = :id AND p.user.id = :userId")
    boolean existsByIdAndAuthorId(@Param("id") Long id, @Param("userId") Long userId);
}
