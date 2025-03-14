package com.j30n.stoblyx.adapter.out.persistence.content;

import com.j30n.stoblyx.application.port.out.content.ContentPort;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.model.ContentInteraction;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.repository.ShortFormContentRepository;
import com.j30n.stoblyx.domain.repository.ContentLikeRepository;
import com.j30n.stoblyx.domain.repository.ContentInteractionRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
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
    private final ContentInteractionRepository contentInteractionRepository;
    private final UserRepository userRepository;

    @Override
    public ShortFormContent save(ShortFormContent content) {
        return contentRepository.save(content);
    }

    @Override
    public Optional<ShortFormContent> findById(Long id) {
        return contentRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public void delete(ShortFormContent content) {
        content.delete();
        contentRepository.save(content);
    }

    @Override
    public Page<ShortFormContent> findAll(Pageable pageable) {
        return contentRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Page<ShortFormContent> findByUserId(Long userId, Pageable pageable) {
        return contentRepository.findByQuote_User_IdAndIsDeletedFalse(userId, pageable);
    }

    @Override
    public Page<ShortFormContent> findByBookId(Long bookId, Pageable pageable) {
        return contentRepository.findByBook_IdAndIsDeletedFalse(bookId, pageable);
    }

    @Override
    public Page<ShortFormContent> search(String keyword, Pageable pageable) {
        return contentRepository.findBySubtitlesContainingAndIsDeletedFalse(keyword, pageable);
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
        return contentLikeRepository.existsByContent_IdAndUser_Id(contentId, userId);
    }

    @Override
    public void updateStatus(Long id, String status) {
        contentRepository.findById(id).ifPresent(content -> {
            content.updateStatus(ContentStatus.valueOf(status.toUpperCase()));
            contentRepository.save(content);
        });
    }

    @Override
    public Page<ShortFormContent> findByStatus(ContentStatus status, Pageable pageable) {
        return contentRepository.findByStatusAndIsDeletedFalse(status, pageable);
    }

    @Override
    public void saveInteraction(Long userId, Long contentId, String interactionType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        
        ShortFormContent content = contentRepository.findByIdAndIsDeletedFalse(contentId)
            .orElseThrow(() -> new IllegalArgumentException("콘텐츠를 찾을 수 없습니다. ID: " + contentId));
        
        // 기존 상호작용 조회
        Optional<ContentInteraction> existingInteraction = 
            contentInteractionRepository.findByUserIdAndContentId(userId, contentId);
        
        ContentInteraction interaction;
        
        if (existingInteraction.isPresent()) {
            // 기존 상호작용이 있는 경우 업데이트
            interaction = existingInteraction.get();
            
            // 상호작용 유형에 따라 처리
            switch (interactionType) {
                case "view":
                    interaction.updateViewedAt();
                    break;
                case "like":
                    interaction.toggleLike();
                    break;
                case "bookmark":
                    interaction.toggleBookmark();
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 상호작용 유형입니다: " + interactionType);
            }
        } else {
            // 새로운 상호작용 생성
            interaction = new ContentInteraction(user, content);
            
            // 상호작용 유형에 따라 처리
            switch (interactionType) {
                case "view":
                    // 기본적으로 조회 시간이 설정됨
                    break;
                case "like":
                    interaction.toggleLike();
                    break;
                case "bookmark":
                    interaction.toggleBookmark();
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 상호작용 유형입니다: " + interactionType);
            }
        }
        
        contentInteractionRepository.save(interaction);
    }
}
