package com.j30n.stoblyx.application.service.bookmark;

import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkResponse;
import com.j30n.stoblyx.adapter.in.web.dto.bookmark.BookmarkStatusResponse;
import com.j30n.stoblyx.domain.model.ContentBookmark;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.ContentBookmarkRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private static final String USER_NOT_FOUND_MSG = "사용자를 찾을 수 없습니다. ID: ";

    private final ContentBookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 북마크 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getBookmarks(Long userId, String type, Pageable pageable) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId));

        // 북마크 목록 조회 로직 구현
        // 실제로는 JPA 쿼리를 사용하여 북마크와 콘텐츠를 조인하여 조회해야 함
        Page<ContentBookmark> bookmarks = bookmarkRepository.findByUserId(userId, pageable);
        
        return bookmarks.map(bookmark -> {
            ShortFormContent content = bookmark.getContent();
            return new BookmarkResponse(
                bookmark.getId(),
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getThumbnailUrl(),
                bookmark.getCreatedAt()
            );
        });
    }

    /**
     * 북마크 상태를 확인합니다.
     */
    @Transactional(readOnly = true)
    public BookmarkStatusResponse checkBookmarkStatus(Long userId, Long contentId) {
        boolean isBookmarked = bookmarkRepository.existsByUserIdAndContentId(userId, contentId);
        return new BookmarkStatusResponse(isBookmarked);
    }

    /**
     * 북마크를 일괄 삭제합니다.
     */
    @Transactional
    public void deleteBookmarks(Long userId, List<Long> contentIds) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + userId));

        for (Long contentId : contentIds) {
            bookmarkRepository.deleteByUserIdAndContentId(userId, contentId);
        }
    }
} 