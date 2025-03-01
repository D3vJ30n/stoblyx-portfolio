package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.j30n.stoblyx.application.port.out.ai.AIPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * AI 어댑터
 * 외부 AI 서비스와의 통신을 구현합니다.
 * 비동기 처리 및 폴백 전략이 적용되었습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIAdapter implements AIPort {

    private final PexelsClient pexelsClient;
    private final TTSClient ttsClient;
    private final BGMClient bgmClient;
    
    // 비동기 작업 타임아웃 설정
    private static final long ASYNC_TIMEOUT_SECONDS = 10;

    // 폴백 리소스 메서드 대신 상수 선언
    private static final String FALLBACK_IMAGE = "static/images/fallback/book-cover.jpg";
    private static final String FALLBACK_VIDEO = "static/videos/fallback/book-animation.mp4";
    private static final String FALLBACK_AUDIO = "static/audio/fallback/default-narration.mp3";
    private static final String FALLBACK_BGM = "static/bgm/neutral.mp3";

    /**
     * 텍스트 기반 이미지 검색
     * 비동기 처리 및 폴백 전략 적용
     */
    @Override
    public String searchImage(String query) {
        log.info("이미지 검색 요청: {}", query);
        
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return pexelsClient.searchImage(query);
                } catch (Exception e) {
                    log.error("이미지 검색 중 오류 발생: {}", e.getMessage());
                    throw e;
                }
            }).get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("이미지 검색 시간 초과: {}", query);
            return FALLBACK_IMAGE;
        } catch (InterruptedException e) {
            // 인터럽트 상태 복원
            Thread.currentThread().interrupt();
            log.error("이미지 검색 중단: {}", e.getMessage());
            return FALLBACK_IMAGE;
        } catch (Exception e) {
            log.error("이미지 검색 실패: {}", e.getMessage());
            return FALLBACK_IMAGE;
        }
    }

    /**
     * 텍스트 기반 비디오 검색
     * 비동기 처리 및 폴백 전략 적용
     */
    @Override
    public String searchVideo(String query) {
        log.info("비디오 검색 요청: {}", query);
        
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return pexelsClient.searchVideo(query);
                } catch (Exception e) {
                    log.error("비디오 검색 중 오류 발생: {}", e.getMessage());
                    throw e;
                }
            }).get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("비디오 검색 시간 초과: {}", query);
            return FALLBACK_VIDEO;
        } catch (InterruptedException e) {
            // 인터럽트 상태 복원
            Thread.currentThread().interrupt();
            log.error("비디오 검색 중단: {}", e.getMessage());
            return FALLBACK_VIDEO;
        } catch (Exception e) {
            log.error("비디오 검색 실패: {}", e.getMessage());
            return FALLBACK_VIDEO;
        }
    }

    /**
     * 텍스트를 음성으로 변환
     * 비동기 처리 및 폴백 전략 적용
     */
    @Override
    public String generateSpeech(String text) {
        log.info("음성 생성 요청: {}", text.substring(0, Math.min(50, text.length())));
        
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return ttsClient.generateSpeech(text);
                } catch (Exception e) {
                    log.error("음성 생성 중 오류 발생: {}", e.getMessage());
                    throw e;
                }
            }).get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 인터럽트 상태 복원
            Thread.currentThread().interrupt();
            log.error("음성 생성 중단: {}", e.getMessage());
            return FALLBACK_AUDIO;
        } catch (Exception e) {
            log.error("음성 생성 실패: {}", e.getMessage());
            return FALLBACK_AUDIO;
        }
    }

    /**
     * 상황에 맞는 BGM 선택
     * 텍스트 감정에 기반한 BGM 선택
     */
    @Override
    public String selectBGM() {
        log.info("기본 BGM 선택 요청");
        
        try {
            return bgmClient.selectBGM();
        } catch (Exception e) {
            log.error("BGM 선택 실패: {}", e.getMessage());
            return FALLBACK_BGM;
        }
    }
    
    /**
     * 텍스트 분석 기반 BGM 선택
     */
    @Override
    public String selectBGMByText(String text) {
        log.info("텍스트 기반 BGM 선택 요청: {}", text.substring(0, Math.min(50, text.length())));
        
        try {
            return bgmClient.selectBGMByText(text);
        } catch (Exception e) {
            log.error("텍스트 기반 BGM 선택 실패: {}", e.getMessage());
            return FALLBACK_BGM;
        }
    }
    
    /**
     * 모든 AI 기능을 통합 - 책 데이터 기반 멀티미디어 생성
     */
    @Async
    @Override
    public CompletableFuture<BookMultimediaDTO> generateBookMultimedia(String title, String description) {
        log.info("책 멀티미디어 생성 요청: {}", title);
        
        // 병렬 처리를 위한 CompletableFuture 생성
        CompletableFuture<String> imageFuture = CompletableFuture.supplyAsync(() -> 
            searchImage(title + " " + description.substring(0, Math.min(50, description.length())))
        );
        
        CompletableFuture<String> videoFuture = CompletableFuture.supplyAsync(() -> 
            searchVideo(title)
        );
        
        CompletableFuture<String> audioFuture = CompletableFuture.supplyAsync(() -> 
            generateSpeech(description)
        );
        
        CompletableFuture<String> bgmFuture = CompletableFuture.supplyAsync(() -> 
            selectBGMByText(description)
        );
        
        // 모든 Future를 결합하여 결과 생성
        return CompletableFuture.allOf(imageFuture, videoFuture, audioFuture, bgmFuture)
            .thenApply(v -> {
                try {
                    return new BookMultimediaDTO(
                        imageFuture.get(2, TimeUnit.SECONDS),
                        videoFuture.get(2, TimeUnit.SECONDS),
                        audioFuture.get(2, TimeUnit.SECONDS),
                        bgmFuture.get(2, TimeUnit.SECONDS)
                    );
                } catch (InterruptedException e) {
                    // 인터럽트 상태 복원
                    Thread.currentThread().interrupt();
                    log.error("멀티미디어 데이터 조합 중 중단: {}", e.getMessage());
                    return createPartialResult(imageFuture, videoFuture, audioFuture, bgmFuture);
                } catch (Exception e) {
                    log.error("멀티미디어 데이터 조합 중 오류: {}", e.getMessage());
                    return createPartialResult(imageFuture, videoFuture, audioFuture, bgmFuture);
                }
            });
    }
    
    /**
     * 일부 요청이 실패했을 때 부분적 결과 생성
     */
    private BookMultimediaDTO createPartialResult(
            CompletableFuture<String> imageFuture,
            CompletableFuture<String> videoFuture,
            CompletableFuture<String> audioFuture,
            CompletableFuture<String> bgmFuture) {
        
        String image = safeGetFuture(imageFuture, this::getFallbackImage);
        String video = safeGetFuture(videoFuture, this::getFallbackVideo);
        String audio = safeGetFuture(audioFuture, this::getFallbackAudio);
        String bgm = safeGetFuture(bgmFuture, this::getFallbackBGM);
        
        return new BookMultimediaDTO(image, video, audio, bgm);
    }
    
    /**
     * Future에서 안전하게 결과 가져오기 (실패 시 폴백 사용)
     */
    private <T> T safeGetFuture(CompletableFuture<T> future, java.util.function.Supplier<T> fallback) {
        try {
            return future.isDone() ? future.get() : fallback.get();
        } catch (InterruptedException e) {
            // 인터럽트 상태 복원
            Thread.currentThread().interrupt();
            return fallback.get();
        } catch (Exception e) {
            return fallback.get();
        }
    }
    
    // 폴백 리소스 메서드들
    private String getFallbackImage() {
        return FALLBACK_IMAGE;
    }
    
    private String getFallbackVideo() {
        return FALLBACK_VIDEO;
    }
    
    private String getFallbackAudio() {
        return FALLBACK_AUDIO;
    }
    
    private String getFallbackBGM() {
        return FALLBACK_BGM;
    }
}
