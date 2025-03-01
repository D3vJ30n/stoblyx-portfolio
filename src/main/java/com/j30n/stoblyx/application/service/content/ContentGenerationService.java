package com.j30n.stoblyx.application.service.content;

import com.j30n.stoblyx.application.port.in.content.ContentGenerationUseCase;
import com.j30n.stoblyx.adapter.out.persistence.ai.BGMClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.PexelsClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.TTSClient;
import com.j30n.stoblyx.domain.enums.ContentStatus;
import com.j30n.stoblyx.domain.model.MediaResource;
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

            // 1. 이미지/비디오 검색 (랜덤하게 선택)
            String imagePrompt = generateImagePrompt(quote);
            String thumbnailUrl;
            String videoUrl = null;
            
            // 랜덤하게 이미지 또는 비디오 선택
            boolean useVideo = Math.random() < 0.5; // 50% 확률로 비디오 사용
            
            if (useVideo) {
                // 비디오 사용
                videoUrl = pexelsClient.searchVideo(imagePrompt);
                thumbnailUrl = pexelsClient.searchImage(imagePrompt); // 썸네일용 이미지
                log.debug("비디오 검색 완료: {}", videoUrl);
            } else {
                // 이미지만 사용
                thumbnailUrl = pexelsClient.searchImage(imagePrompt);
                log.debug("이미지 검색 완료: {}", thumbnailUrl);
            }

            // 2. 음성 생성
            String audioUrl = ttsClient.generateSpeech(quote.getContent());
            log.debug("음성 생성 완료: {}", audioUrl);

            // 3. BGM 선택 (텍스트 내용에 따라 감정 분석하여 선택)
            String bgmUrl = bgmClient.selectBGMByText(quote.getContent());
            log.debug("BGM 선택 완료: {}", bgmUrl);

            // 4. 자막 생성
            String subtitles = generateSubtitles(quote.getContent());
            log.debug("자막 생성 완료");

            // 5. 콘텐츠 생성
            ShortFormContent content = ShortFormContent.builder()
                .book(quote.getBook())
                .quote(quote)
                .title(quote.getBook().getTitle() + " - 인용구") // 기본 제목 설정
                .description(quote.getContent()) // 기본 설명 설정
                .build();

            // 6. 미디어 리소스 추가
            // 오디오 리소스 추가
            MediaResource audioResource = MediaResource.builder()
                .type(MediaResource.MediaType.AUDIO)
                .url(audioUrl)
                .content(content)
                .build();
            content.addMediaResource(audioResource);

            // 이미지 리소스 추가
            MediaResource imageResource = MediaResource.builder()
                .type(MediaResource.MediaType.IMAGE)
                .url(thumbnailUrl)
                .thumbnailUrl(thumbnailUrl)
                .content(content)
                .build();
            content.addMediaResource(imageResource);
            
            // 비디오 리소스 추가 (비디오를 사용하는 경우)
            if (useVideo && videoUrl != null) {
                MediaResource videoResource = MediaResource.builder()
                    .type(MediaResource.MediaType.VIDEO)
                    .url(videoUrl)
                    .thumbnailUrl(thumbnailUrl)
                    .content(content)
                    .build();
                content.addMediaResource(videoResource);
            }

            // BGM 리소스 추가
            MediaResource bgmResource = MediaResource.builder()
                .type(MediaResource.MediaType.BGM)
                .url(bgmUrl)
                .content(content)
                .build();
            content.addMediaResource(bgmResource);
            
            // 자막 리소스 추가
            MediaResource subtitleResource = MediaResource.builder()
                .type(MediaResource.MediaType.SUBTITLE)
                .url("#") // 자막은 URL이 없으므로 임시값
                .description(subtitles) // 자막 내용을 설명에 저장
                .content(content)
                .build();
            content.addMediaResource(subtitleResource);

            content.updateStatus(ContentStatus.COMPLETED);
            contentRepository.save(content);
            log.info("콘텐츠 생성 완료 - Content ID: {}", content.getId());

        } catch (Exception e) {
            log.error("콘텐츠 생성 실패 - Quote ID: {}", quote.getId(), e);
            ShortFormContent failedContent = ShortFormContent.builder()
                .book(quote.getBook())
                .quote(quote)
                .title(quote.getBook().getTitle() + " - 인용구") // 기본 제목 설정
                .description("생성 실패") // 실패 메시지
                .status(ContentStatus.FAILED)
                .build();
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
        try {
            log.info("Generating image for quote: {}", quote.getId());
            String imagePrompt = generateImagePrompt(quote);
            return pexelsClient.searchImage(imagePrompt);
        } catch (Exception e) {
            log.error("Image generation failed for quote: {}", quote.getId(), e);
            return "https://placeholder.com/image/" + quote.getId();
        }
    }

    @Override
    public String generateAudio(Quote quote) {
        try {
            log.info("Generating audio for quote: {}", quote.getId());
            return ttsClient.generateSpeech(quote.getContent());
        } catch (Exception e) {
            log.error("Audio generation failed for quote: {}", quote.getId(), e);
            return "https://placeholder.com/audio/" + quote.getId();
        }
    }

    @Override
    public String generateVideo(Quote quote) {
        try {
            log.info("Generating video for quote: {}", quote.getId());
            // 랜덤하게 비디오를 생성하거나 이미지를 반환
            if (Math.random() < 0.5) {
                // 50% 확률로 비디오 생성
                String imagePrompt = generateImagePrompt(quote);
                return pexelsClient.searchVideo(imagePrompt);
            } else {
                // 50% 확률로 이미지 생성
                String imagePrompt = generateImagePrompt(quote);
                return pexelsClient.searchImage(imagePrompt);
            }
        } catch (Exception e) {
            log.error("Video generation failed for quote: {}", quote.getId(), e);
            return "https://placeholder.com/video/" + quote.getId();
        }
    }
}