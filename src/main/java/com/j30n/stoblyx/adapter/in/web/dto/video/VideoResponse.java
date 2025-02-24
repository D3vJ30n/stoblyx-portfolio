package com.j30n.stoblyx.adapter.in.web.dto.video;

import com.j30n.stoblyx.domain.model.Video;
import com.j30n.stoblyx.domain.model.VideoStatus;

public record VideoResponse(
    Long id,
    Long quoteId,
    String videoUrl,
    String thumbnailUrl,
    VideoStatus status,
    String style,
    String bgmType,
    Integer duration
) {
    public static VideoResponse from(Video video) {
        return new VideoResponse(
            video.getId(),
            video.getQuote().getId(),
            video.getVideoUrl(),
            video.getThumbnailUrl(),
            video.getStatus(),
            video.getStyle(),
            video.getBgmType(),
            video.getDuration()
        );
    }
} 