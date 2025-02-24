package com.j30n.stoblyx.adapter.in.web.dto.summary;

import com.j30n.stoblyx.domain.model.Summary;
import java.time.LocalDateTime;

public record SummaryResponse(
    Long id,
    Long bookId,
    String content,
    String chapter,
    String page,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    public static SummaryResponse from(Summary summary) {
        return new SummaryResponse(
            summary.getId(),
            summary.getBook().getId(),
            summary.getContent(),
            summary.getChapter(),
            summary.getPage(),
            summary.getCreatedAt(),
            summary.getModifiedAt()
        );
    }
} 