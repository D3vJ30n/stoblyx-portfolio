package com.j30n.stoblyx.application.port.in.ai;

/**
 * AI 서비스 유스케이스
 * 텍스트 기반 이미지/비디오 검색과 음성, BGM 생성 기능을 제공합니다.
 */
public interface AIUseCase {
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
     * 상황에 맞는 BGM을 선택합니다.
     *
     * @return 선택된 BGM의 URL
     */
    String selectBGM();
}
