package com.j30n.stoblyx.application.service.video;

import com.j30n.stoblyx.adapter.in.web.dto.video.VideoCreateRequest;
import com.j30n.stoblyx.adapter.in.web.dto.video.VideoResponse;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.model.Video;
import com.j30n.stoblyx.domain.repository.QuoteRepository;
import com.j30n.stoblyx.domain.repository.UserRepository;
import com.j30n.stoblyx.domain.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public VideoResponse createVideo(Long userId, VideoCreateRequest request) {
        log.debug("비디오 생성: userId={}, request={}", userId, request);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            
        Quote quote = quoteRepository.findById(request.quoteId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인용구입니다."));

        if (!quote.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 인용구에 대한 권한이 없습니다.");
        }

        if (videoRepository.existsByQuoteId(request.quoteId())) {
            throw new IllegalArgumentException("이미 비디오가 생성된 인용구입니다.");
        }

        Video video = Video.builder()
            .quote(quote)
            .style(request.style())
            .bgmType(request.bgmType())
            .build();

        videoRepository.save(video);
        return VideoResponse.from(video);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getVideo(Long userId, Long videoId) {
        log.debug("비디오 조회: userId={}, videoId={}", userId, videoId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 비디오입니다."));

        if (!video.getQuote().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 비디오에 대한 권한이 없습니다.");
        }

        return VideoResponse.from(video);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getVideoByQuoteId(Long userId, Long quoteId) {
        log.debug("인용구로 비디오 조회: userId={}, quoteId={}", userId, quoteId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인용구입니다."));

        if (!quote.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 인용구에 대한 권한이 없습니다.");
        }

        Video video = videoRepository.findByQuoteId(quoteId)
            .orElseThrow(() -> new IllegalArgumentException("해당 인용구의 비디오가 존재하지 않습니다."));

        return VideoResponse.from(video);
    }
} 