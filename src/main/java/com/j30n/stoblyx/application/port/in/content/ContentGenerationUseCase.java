package com.j30n.stoblyx.application.port.in.content;

import com.j30n.stoblyx.domain.model.Quote;

public interface ContentGenerationUseCase {
    String generateImage(Quote quote);
    String generateAudio(Quote quote);
    String generateVideo(Quote quote);
}
