package com.j30n.stoblyx.config;

import com.j30n.stoblyx.adapter.out.persistence.ai.BGMClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.PexelsClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.TTSClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestKoBartConfig {

    @Bean
    @Primary
    public PexelsClient pexelsClient() {
        return new PexelsClient(new RestTemplate()) {
            @Override
            public String searchImage(String query) {
                return "http://example.com/test-image.jpg";
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
    public BGMClient bgmClient(ResourceLoader resourceLoader) {
        return new BGMClient(resourceLoader) {
            @Override
            public String selectBGM() {
                return "http://example.com/test-bgm.mp3";
            }
        };
    }
}