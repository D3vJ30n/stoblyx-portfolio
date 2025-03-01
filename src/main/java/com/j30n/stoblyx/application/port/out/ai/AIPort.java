package com.j30n.stoblyx.application.port.out.ai;

import com.j30n.stoblyx.adapter.out.persistence.ai.BookMultimediaDTO;
import java.util.concurrent.CompletableFuture;

/**
 * AI 서비스 포트
 * 외부 AI 서비스와의 통신을 담당합니다.
 */
public interface AIPort {
    /**
     * 텍스트를 기반으로 이미지를 검색합니다.
     *
     * @param query 검색할 텍스트
     * @return 검색된 이미지의 URL
     */
    String searchImage(String query);

    /**
     * 텍스트를 기반으로 비디오를 검색합니다.
     *
     * @param query 검색할 텍스트
     * @return 검색된 비디오의 URL
     */
    String searchVideo(String query);

    /**
     * 텍스트를 음성으로 변환합니다.
     *
     * @param text 음성으로 변환할 텍스트
     * @return 생성된 음성 파일의 URL
     */
    String generateSpeech(String text);

    /**
     * 기본 BGM을 선택합니다.
     *
     * @return 선택된 BGM의 URL
     */
    String selectBGM();
    
    /**
     * 텍스트 분석을 통해 적절한 BGM을 선택합니다.
     *
     * @param text 분석할 텍스트
     * @return 선택된 BGM의 URL
     */
    String selectBGMByText(String text);
    
    /**
     * 책 데이터를 기반으로 멀티미디어 요소를 생성합니다.
     * 비동기 처리됩니다.
     *
     * @param title 책 제목
     * @param description 책 설명
     * @return 생성된 멀티미디어 요소를 담은 DTO
     */
    CompletableFuture<BookMultimediaDTO> generateBookMultimedia(String title, String description);
}
