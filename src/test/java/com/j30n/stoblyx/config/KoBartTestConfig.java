package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.out.persistence.ai.BGMClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.PexelsClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.TTSClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class KoBartTestConfig {

    @Bean
    @Primary
    public PexelsClient pexelsClient() {
        return new PexelsClient("test-api-key", new RestTemplate()) {
            @Override
            public String searchImage(String query) {
                return "http://example.com/test-image.jpg";
            }

            @Override
            public String searchVideo(String query) {
                return "http://example.com/test-video.mp4";
            }
        };
    }

    @Bean
    @Primary
    public TTSClient ttsClient() {
        return new TTSClient() {
            @Override
            public String generateSpeech(String text) {
                return "http://example.com/test-audio.mp3";
            }
        };
    }

    @Bean
    @Primary
    public BGMClient bgmClient() {
        return new BGMClient() {
            public String selectBGM() {
                return "http://example.com/test-bgm.mp3";
            }

            public String selectBGMByMood(String mood) {
                return "http://example.com/test-bgm.mp3";
            }

            public String getRandomBGM() {
                return "http://example.com/test-bgm.mp3";
            }
        };
    }
}