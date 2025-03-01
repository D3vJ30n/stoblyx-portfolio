package com.j30n.stoblyx.application.port.in.post;

import com.j30n.stoblyx.adapter.in.web.dto.post.CreatePostRequest;
import com.j30n.stoblyx.adapter.in.web.dto.post.PostResponse;
import com.j30n.stoblyx.adapter.in.web.dto.post.UpdatePostRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 게시물 관련 유스케이스 인터페이스
 */
public interface PostUseCase {

    /**
     * 게시물 생성
     *
     * @param request 게시물 생성 요청 DTO
     * @param userId 작성자 ID
     * @return 생성된 게시물 응답 DTO
     */
    PostResponse createPost(CreatePostRequest request, Long userId);

    /**
     * 게시물 수정
     *
     * @param postId 게시물 ID
     * @param request 게시물 수정 요청 DTO
     * @param userId 수정자 ID
     * @return 수정된 게시물 응답 DTO
     */
    PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId);

    /**
     * 게시물 삭제
     *
     * @param postId 게시물 ID
     * @param userId 삭제자 ID
     */
    void deletePost(Long postId, Long userId);

    /**
     * 게시물 상세 조회
     *
     * @param postId 게시물 ID
     * @return 게시물 응답 DTO
     */
    PostResponse getPost(Long postId);

    /**
     * 게시물 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @return 게시물 응답 DTO 페이지
     */
    Page<PostResponse> getPosts(Pageable pageable);

    /**
     * 사용자별 게시물 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return 게시물 응답 DTO 페이지
     */
    Page<PostResponse> getPostsByUser(Long userId, Pageable pageable);
}
