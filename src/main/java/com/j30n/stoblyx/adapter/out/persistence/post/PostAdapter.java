package com.j30n.stoblyx.adapter.out.persistence.post;

import com.j30n.stoblyx.application.port.out.post.PostPort;
import com.j30n.stoblyx.domain.model.Post;
import com.j30n.stoblyx.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 게시물 영속성 어댑터
 */
@Component
@RequiredArgsConstructor
public class PostAdapter implements PostPort {

    private final PostRepository postRepository;

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> findPostById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public void deletePost(Post post) {
        post.delete();
        postRepository.save(post);
    }

    @Override
    public Page<Post> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> findPostsByUserId(Long userId, Pageable pageable) {
        return postRepository.findByAuthorId(userId, pageable);
    }

    @Override
    public boolean existsByIdAndAuthorId(Long id, Long authorId) {
        return postRepository.existsByIdAndAuthorId(id, authorId);
    }
}
