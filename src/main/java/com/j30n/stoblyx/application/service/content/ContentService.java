package com.j30n.stoblyx.application.service.content;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import com.j30n.stoblyx.application.port.in.content.ContentUseCase;
import com.j30n.stoblyx.application.port.out.content.ContentPort;
import com.j30n.stoblyx.domain.model.ContentStatus;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.model.ContentBookmark;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.ContentBookmarkRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService implements ContentUseCase {

    private final ContentPort contentPort;
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final ContentBookmarkRepository bookmarkRepository;
    private final ContentGenerationService contentGenerationService;

    @Override
    @Transactional
    public ContentResponse generateContent(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("인용구를 찾을 수 없습니다. ID: " + quoteId));

        ShortFormContent content = ShortFormContent.builder()
            .quote(quote)
            .book(quote.getBook())
            .imageUrl(contentGenerationService.generateImage(quote))
            .audioUrl(contentGenerationService.generateAudio(quote))
            .videoUrl(contentGenerationService.generateVideo(quote))
            .thumbnailUrl(contentGenerationService.generateImage(quote)) // 썸네일로 같은 이미지 사용
            .subtitles(quote.getContent())
            .status(ContentStatus.COMPLETED)
            .build();

        content = contentPort.save(content);
        return ContentResponse.from(content, false, false); // 새로 생성된 콘텐츠는 아직 좋아요나 북마크되지 않음
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getTrendingContents(Pageable pageable) {
        return contentPort.findTrendingContents(pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getRecommendedContents(Long userId, Pageable pageable) {
        return contentPort.findRecommendedContents(userId, pageable)
            .map(content -> {
                boolean isLiked = contentPort.isLikedByUser(content.getId(), userId);
                boolean isBookmarked = bookmarkRepository.existsByUserIdAndContentId(userId, content.getId());
                return ContentResponse.from(content, isLiked, isBookmarked);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public ContentResponse getContent(Long id) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + id));
        return ContentResponse.from(content, false, false);
    }

    @Override
    @Transactional
    public void toggleLike(Long userId, Long contentId) {
        ShortFormContent content = contentPort.findById(contentId)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + contentId));
        content.updateLikeCount(1); // 실제로는 좋아요 토글 로직이 필요
        contentPort.save(content);
    }

    @Override
    @Transactional
    public void toggleBookmark(Long userId, Long contentId) {
        ShortFormContent content = contentPort.findById(contentId)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + contentId));

        if (bookmarkRepository.existsByUserIdAndContentId(userId, contentId)) {
            bookmarkRepository.deleteByUserIdAndContentId(userId, contentId);
        } else {
            ContentBookmark bookmark = ContentBookmark.builder()
                .user(userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId)))
                .content(content)
                .build();
            bookmarkRepository.save(bookmark);
        }
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + id));
        content.incrementViewCount();
        contentPort.save(content);
    }

    @Override
    @Transactional
    public void incrementShareCount(Long id) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + id));
        content.incrementShareCount();
        contentPort.save(content);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getAllContents(Pageable pageable) {
        return contentPort.findAll(pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getContentsByUser(Long userId, Pageable pageable) {
        return contentPort.findByUserId(userId, pageable)
            .map(content -> {
                boolean isLiked = contentPort.isLikedByUser(content.getId(), userId);
                boolean isBookmarked = bookmarkRepository.existsByUserIdAndContentId(userId, content.getId());
                return ContentResponse.from(content, isLiked, isBookmarked);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getContentsByBook(Long bookId, Pageable pageable) {
        return contentPort.findByBookId(bookId, pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> searchContents(String keyword, Pageable pageable) {
        return contentPort.search(keyword, pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }

    @Override
    @Transactional
    public void deleteContent(Long id) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + id));
        content.delete(); // 소프트 삭제 사용
        contentPort.save(content);
    }

    @Override
    @Transactional
    public void updateContentStatus(Long id, String status) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("콘텐츠를 찾을 수 없습니다. ID: " + id));
        content.updateStatus(ContentStatus.valueOf(status.toUpperCase()));
        contentPort.save(content);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getPopularContents(Pageable pageable) {
        return contentPort.findPopularContents(pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }
}