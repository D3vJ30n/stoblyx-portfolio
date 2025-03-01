package com.j30n.stoblyx.application.service.post;

import com.j30n.stoblyx.adapter.in.web.dto.post.CreatePostRequest;
import com.j30n.stoblyx.adapter.in.web.dto.post.PostResponse;
import com.j30n.stoblyx.adapter.in.web.dto.post.UpdatePostRequest;
import com.j30n.stoblyx.application.port.in.post.PostUseCase;
import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.application.port.out.post.PostPort;
import com.j30n.stoblyx.domain.model.Post;
import com.j30n.stoblyx.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시물 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements PostUseCase {

    private final PostPort postPort;
    private final AuthPort authPort;

    @Override
    @Transactional
    public PostResponse createPost(CreatePostRequest request, Long userId) {
        User author = authPort.findUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Post post = Post.builder()
            .title(request.title())
            .content(request.content())
            .thumbnailUrl(request.thumbnailUrl())
            .author(author)
            .build();

        Post savedPost = postPort.savePost(post);
        log.info("게시물 생성 완료: postId={}, userId={}", savedPost.getId(), userId);
        return PostResponse.from(savedPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId) {
        Post post = postPort.findPostById(postId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("게시물 수정 권한이 없습니다.");
        }

        post.update(request.title(), request.content(), request.thumbnailUrl());
        Post updatedPost = postPort.savePost(post);
        log.info("게시물 수정 완료: postId={}, userId={}", postId, userId);
        return PostResponse.from(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postPort.findPostById(postId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("게시물 삭제 권한이 없습니다.");
        }

        postPort.deletePost(post);
        log.info("게시물 삭제 완료: postId={}, userId={}", postId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postPort.findPostById(postId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        return PostResponse.from(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postPort.findAllPosts(pageable)
            .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUser(Long userId, Pageable pageable) {
        if (!authPort.findUserById(userId).isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return postPort.findPostsByUserId(userId, pageable)
            .map(PostResponse::from);
    }
}
