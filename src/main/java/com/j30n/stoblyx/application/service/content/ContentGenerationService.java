package com.j30n.stoblyx.application.service.content;

import com.j30n.stoblyx.application.port.in.content.ContentGenerationUseCase;
import com.j30n.stoblyx.adapter.out.persistence.ai.BGMClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.PexelsClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.TTSClient;
import com.j30n.stoblyx.domain.model.ContentStatus;
import com.j30n.stoblyx.domain.model.Quote;
import com.j30n.stoblyx.domain.model.ShortFormContent;
import com.j30n.stoblyx.domain.repository.ShortFormContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentGenerationService implements ContentGenerationUseCase {

    private final ShortFormContentRepository contentRepository;
    private final TTSClient ttsClient;
    private final BGMClient bgmClient;
    private final PexelsClient pexelsClient;

    @Async
    @Transactional
    public void generateContent(Quote quote) {
        try {
            log.info("콘텐츠 생성 시작 - Quote ID: {}", quote.getId());

            // 1. 이미지 검색
            String imagePrompt = generateImagePrompt(quote);
            String thumbnailUrl = pexelsClient.searchImage(imagePrompt);
            log.debug("이미지 검색 완료: {}", thumbnailUrl);

            // 2. 음성 생성
            String audioUrl = ttsClient.generateSpeech(quote.getContent());
            log.debug("음성 생성 완료: {}", audioUrl);

            // 3. BGM 선택
            String bgmUrl = bgmClient.selectBGM();
            log.debug("BGM 선택 완료: {}", bgmUrl);

            // 4. 자막 생성
            String subtitles = generateSubtitles(quote.getContent());
            log.debug("자막 생성 완료");

            // 5. 콘텐츠 저장
            ShortFormContent content = ShortFormContent.builder()
                .book(quote.getBook())
                .quote(quote)
                .videoUrl(audioUrl)  // 음성을 비디오 URL로 사용
                .thumbnailUrl(thumbnailUrl)  // 검색한 이미지를 썸네일로 사용
                .bgmUrl(bgmUrl)
                .subtitles(subtitles)
                .build();

            content.updateStatus(ContentStatus.COMPLETED);
            contentRepository.save(content);
            log.info("콘텐츠 생성 완료 - Content ID: {}", content.getId());

        } catch (Exception e) {
            log.error("콘텐츠 생성 실패 - Quote ID: {}", quote.getId(), e);
            ShortFormContent failedContent = ShortFormContent.builder()
                .book(quote.getBook())
                .quote(quote)
                .build();
            failedContent.updateStatus(ContentStatus.FAILED);
            contentRepository.save(failedContent);
        }
    }

    private String generateImagePrompt(Quote quote) {
        // 책과 인용구의 내용을 바탕으로 이미지 검색어 생성
        return String.format("%s %s", quote.getBook().getTitle(), quote.getContent());
    }

    private String generateSubtitles(String content) {
        // 자막 생성 로직 (시간 정보 포함)
        return String.format("""
            1
            00:00:00,000 --> 00:00:05,000
            %s
            """, content);
    }

    @Override
    public String generateImage(Quote quote) {
        // TODO: Implement image generation using external service
        // For now, return a placeholder URL
        log.info("Generating image for quote: {}", quote.getId());
        return "https://placeholder.com/image/" + quote.getId();
    }

    @Override
    public String generateAudio(Quote quote) {
        // TODO: Implement audio generation using external service
        // For now, return a placeholder URL
        log.info("Generating audio for quote: {}", quote.getId());
        return "https://placeholder.com/audio/" + quote.getId();
    }

    @Override
    public String generateVideo(Quote quote) {
        // TODO: Implement video generation using external service
        // For now, return a placeholder URL
        log.info("Generating video for quote: {}", quote.getId());
        return "https://placeholder.com/video/" + quote.getId();
    }
}