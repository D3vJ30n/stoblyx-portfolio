package com.j30n.stoblyx.common.util;

import org.springframework.web.util.HtmlUtils;

/**
 * XSS 공격 방지를 위한 유틸리티 클래스
 */
public class XssUtils {

    private XssUtils() {
        // 유틸리티 클래스는 인스턴스화를 방지합니다.
    }

    /**
     * 문자열에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
     *
     * @param input 이스케이프 처리할 문자열
     * @return 이스케이프 처리된 문자열
     */
    public static String escape(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * 이스케이프 처리된 문자열을 원래 문자열로 복원합니다.
     *
     * @param input 복원할 이스케이프 처리된 문자열
     * @return 원래 문자열
     */
    public static String unescape(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlUnescape(input);
    }
} 