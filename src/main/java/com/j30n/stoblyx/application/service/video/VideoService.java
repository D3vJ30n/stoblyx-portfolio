package com.j30n.stoblyx.application.service.video;

import com.j30n.stoblyx.adapter.in.web.dto.video.VideoCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.video.VideoResponse;

public interface VideoService {
    VideoResponse createVideo(Long userId, VideoCreateRequest request);
    VideoResponse getVideo(Long userId, Long videoId);
    VideoResponse getVideoByQuoteId(Long userId, Long quoteId);
} 