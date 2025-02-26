package com.j30n.stoblyx.application.port.out.quote;

public interface QuoteSummaryPort {
    /**
     * KoBART API를 사용하여 문구를 요약합니다.
     *
     * @param content 요약할 문구 내용
     * @return 요약된 문구
     */
    String summarize(String content);
}
