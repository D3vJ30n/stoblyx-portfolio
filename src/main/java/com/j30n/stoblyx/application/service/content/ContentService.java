package com.j30n.stoblyx.application.service.content;

import com.j30n.stoblyx.adapter.in.web.dto.content.ContentResponse;
import com.j30n.stoblyx.application.port.in.content.ContentUseCase;
import com.j30n.stoblyx.application.port.out.content.ContentPort;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.model.MediaResource;
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

    private static final String CONTENT_NOT_FOUND_MSG = "콘텐츠를 찾을 수 없습니다. ID: ";
    private static final String QUOTE_NOT_FOUND_MSG = "인용구를 찾을 수 없습니다. ID: ";
    private static final String USER_NOT_FOUND_MSG = "사용자를 찾을 수 없습니다. ID: ";
    
    private final ContentPort contentPort;
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;
    private final ContentBookmarkRepository bookmarkRepository;
    private final ContentGenerationService contentGenerationService;

    @Override
    @Transactional
    public ContentResponse generateContent(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException(QUOTE_NOT_FOUND_MSG + quoteId));

        ShortFormContent content = ShortFormContent.builder()
            .quote(quote)
            .book(quote.getBook())
            .title(quote.getBook().getTitle() + " - 인용구")
            .description(quote.getContent())
            .status(ContentStatus.COMPLETED)
            .build();

        // 이미지 리소스 추가
        String imageUrl = contentGenerationService.generateImage(quote);
        MediaResource imageResource = MediaResource.builder()
            .type(MediaResource.MediaType.IMAGE)
            .url(imageUrl)
            .thumbnailUrl(imageUrl)
            .content(content)
            .build();
        content.addMediaResource(imageResource);

        // 오디오 리소스 추가
        MediaResource audioResource = MediaResource.builder()
            .type(MediaResource.MediaType.AUDIO)
            .url(contentGenerationService.generateAudio(quote))
            .content(content)
            .build();
        content.addMediaResource(audioResource);

        // 비디오 리소스 추가
        MediaResource videoResource = MediaResource.builder()
            .type(MediaResource.MediaType.VIDEO)
            .url(contentGenerationService.generateVideo(quote))
            .content(content)
            .build();
        content.addMediaResource(videoResource);

        // 자막 리소스 추가
        MediaResource subtitleResource = MediaResource.builder()
            .type(MediaResource.MediaType.SUBTITLE)
            .url("#")
            .description(quote.getContent())
            .content(content)
            .build();
        content.addMediaResource(subtitleResource);

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
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + id));
        return ContentResponse.from(content, false, false);
    }

    @Override
    @Transactional
    public void toggleLike(Long userId, Long contentId) {
        ShortFormContent content = contentPort.findById(contentId)
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + contentId));
        content.updateLikeCount(1); // 실제로는 좋아요 토글 로직이 필요
        contentPort.save(content);
    }

    @Override
    @Transactional
    public void toggleBookmark(Long userId, Long contentId) {
        ShortFormContent content = contentPort.findById(contentId)
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + contentId));

        if (bookmarkRepository.existsByUserIdAndContentId(userId, contentId)) {
            bookmarkRepository.deleteByUserIdAndContentId(userId, contentId);
        } else {
            ContentBookmark bookmark = ContentBookmark.builder()
                .user(userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId)))
                .content(content)
                .build();
            bookmarkRepository.save(bookmark);
        }
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + id));
        content.incrementViewCount();
        contentPort.save(content);
    }

    @Override
    @Transactional
    public void incrementShareCount(Long id) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + id));
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
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + id));
        content.delete(); // 소프트 삭제 사용
        contentPort.save(content);
    }

    @Override
    @Transactional
    public void updateContentStatus(Long id, String status) {
        ShortFormContent content = contentPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(CONTENT_NOT_FOUND_MSG + id));
        content.updateStatus(ContentStatus.valueOf(status.toUpperCase()));
        contentPort.save(content);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getPopularContents(Pageable pageable) {
        return contentPort.findPopularContents(pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }

    /**
     * 상태별 콘텐츠 목록을 조회합니다.
     *
     * @param status 콘텐츠 상태
     * @param pageable 페이징 정보
     * @return 콘텐츠 목록
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> getContentsByStatus(String status, Pageable pageable) {
        return contentPort.findByStatus(ContentStatus.valueOf(status.toUpperCase()), pageable)
            .map(content -> ContentResponse.from(content, false, false));
    }
}