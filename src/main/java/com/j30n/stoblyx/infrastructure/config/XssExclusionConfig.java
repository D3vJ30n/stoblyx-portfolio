package com.j30n.stoblyx.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * XSS 필터를 특정 URL 패턴에서 제외하기 위한 설정 클래스
 */
@Configuration
@ConfigurationProperties(prefix = "xss.filter")
public class XssExclusionConfig {

    /**
     * XSS 필터를 적용하지 않을 URL 패턴 목록
     */
    private List<String> exclusions = new ArrayList<>();

    /**
     * 컴파일된 패턴 목록
     */
    private List<Pattern> compiledPatterns;

    /**
     * XSS 필터를 적용하지 않을 URL 패턴 목록을 반환합니다.
     *
     * @return XSS 필터를 적용하지 않을 URL 패턴 목록
     */
    public List<String> getExclusions() {
        return exclusions;
    }

    /**
     * XSS 필터를 적용하지 않을 URL 패턴 목록을 설정합니다.
     *
     * @param exclusions XSS 필터를 적용하지 않을 URL 패턴 목록
     */
    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
        compilePatterns();
    }

    /**
     * URL 패턴을 컴파일합니다.
     */
    private void compilePatterns() {
        compiledPatterns = new ArrayList<>();
        for (String pattern : exclusions) {
            compiledPatterns.add(Pattern.compile(pattern));
        }
    }

    /**
     * 주어진 URL이 XSS 필터를 적용하지 않을 URL 패턴에 해당하는지 확인합니다.
     *
     * @param url 확인할 URL
     * @return XSS 필터를 적용하지 않을 URL 패턴에 해당하면 true, 그렇지 않으면 false
     */
    public boolean isExcluded(String url) {
        if (compiledPatterns == null) {
            compilePatterns();
        }
        
        for (Pattern pattern : compiledPatterns) {
            if (pattern.matcher(url).matches()) {
                return true;
            }
        }
        
        return false;
    }
} 