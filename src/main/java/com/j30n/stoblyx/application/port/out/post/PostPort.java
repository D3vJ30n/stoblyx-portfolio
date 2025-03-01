package com.j30n.stoblyx.application.port.out.post;

import com.j30n.stoblyx.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 게시물 영속성 포트 인터페이스
 */
public interface PostPort {

    /**
     * 게시물 저장
     *
     * @param post 저장할 게시물
     * @return 저장된 게시물
     */
    Post savePost(Post post);

    /**
     * 게시물 조회
     *
     * @param postId 게시물 ID
     * @return 조회된 게시물
     */
    Optional<Post> findPostById(Long postId);

    /**
     * 게시물 삭제
     *
     * @param post 삭제할 게시물
     */
    void deletePost(Post post);

    /**
     * 게시물 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @return 게시물 목록
     */
    Page<Post> findAllPosts(Pageable pageable);

    /**
     * 사용자별 게시물 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 게시물 목록
     */
    Page<Post> findPostsByUserId(Long userId, Pageable pageable);

    /**
     * 게시물이 특정 작성자의 것인지 확인
     *
     * @param id 게시물 ID
     * @param authorId 작성자 ID
     * @return 작성자의 게시물이면 true, 아니면 false
     */
    boolean existsByIdAndAuthorId(Long id, Long authorId);
}
