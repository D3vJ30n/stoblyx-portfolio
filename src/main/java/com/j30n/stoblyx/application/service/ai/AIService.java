package com.j30n.stoblyx.application.service.ai;

import com.j30n.stoblyx.application.port.in.ai.AIUseCase;
import com.j30n.stoblyx.application.port.out.ai.AIPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AI 서비스
 * 텍스트 기반 이미지/비디오 검색과 음성, BGM 생성 기능을 구현합니다.
 */
@Service
@RequiredArgsConstructor
public class AIService implements AIUseCase {

    private final AIPort aiPort;

    @Override
    @Transactional(readOnly = true)
    public String searchImage(String query) {
        return aiPort.searchImage(query);
    }

    @Override
    @Transactional(readOnly = true)
    public String searchVideo(String query) {
        return aiPort.searchVideo(query);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateSpeech(String text) {
        return aiPort.generateSpeech(text);
    }

    @Override
    @Transactional(readOnly = true)
    public String selectBGM() {
        return aiPort.selectBGM();
    }
}
