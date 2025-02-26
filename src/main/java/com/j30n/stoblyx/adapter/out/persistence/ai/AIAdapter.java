package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.j30n.stoblyx.application.port.out.ai.AIPort;
import com.j30n.stoblyx.adapter.out.persistence.ai.PexelsClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.TTSClient;
import com.j30n.stoblyx.adapter.out.persistence.ai.BGMClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * AI 어댑터
 * 외부 AI 서비스와의 통신을 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class AIAdapter implements AIPort {

    private final PexelsClient pexelsClient;
    private final TTSClient ttsClient;
    private final BGMClient bgmClient;

    @Override
    public String searchImage(String query) {
        return pexelsClient.searchImage(query);
    }

    @Override
    public String searchVideo(String query) {
        return pexelsClient.searchVideo(query);
    }

    @Override
    public String generateSpeech(String text) {
        return ttsClient.generateSpeech(text);
    }

    @Override
    public String selectBGM() {
        return bgmClient.selectBGM();
    }
}
