package com.j30n.stoblyx.adapter.out.persistence.content;

import com.j30n.stoblyx.application.port.out.content.ContentPort;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.repository.ShortFormContentRepository;
import com.j30n.stoblyx.domain.repository.ContentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContentPersistenceAdapter implements ContentPort {

    private final ShortFormContentRepository contentRepository;
    private final ContentLikeRepository contentLikeRepository;

    @Override
    public ShortFormContent save(ShortFormContent content) {
        return contentRepository.save(content);
    }

    @Override
    public Optional<ShortFormContent> findById(Long id) {
        return contentRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public void delete(ShortFormContent content) {
        content.delete();
        contentRepository.save(content);
    }

    @Override
    public Page<ShortFormContent> findAll(Pageable pageable) {
        return contentRepository.findByDeletedFalse(pageable);
    }

    @Override
    public Page<ShortFormContent> findByUserId(Long userId, Pageable pageable) {
        return contentRepository.findByQuote_User_IdAndDeletedFalse(userId, pageable);
    }

    @Override
    public Page<ShortFormContent> findByBookId(Long bookId, Pageable pageable) {
        return contentRepository.findByBook_IdAndDeletedFalse(bookId, pageable);
    }

    @Override
    public Page<ShortFormContent> search(String keyword, Pageable pageable) {
        return contentRepository.findBySubtitlesContainingAndDeletedFalse(keyword, pageable);
    }

    @Override
    public Page<ShortFormContent> findTrendingContents(Pageable pageable) {
        return contentRepository.findTrendingContents(pageable);
    }

    @Override
    public Page<ShortFormContent> findPopularContents(Pageable pageable) {
        return contentRepository.findPopularContents(pageable);
    }

    @Override
    public Page<ShortFormContent> findRecommendedContents(Long userId, Pageable pageable) {
        return contentRepository.findRecommendedContents(userId, pageable);
    }

    @Override
    public boolean isLikedByUser(Long contentId, Long userId) {
        return contentLikeRepository.existsByContentIdAndUserId(contentId, userId);
    }

    @Override
    public void updateStatus(Long id, String status) {
        contentRepository.findById(id).ifPresent(content -> {
            content.updateStatus(ContentStatus.valueOf(status.toUpperCase()));
            contentRepository.save(content);
        });
    }
}
