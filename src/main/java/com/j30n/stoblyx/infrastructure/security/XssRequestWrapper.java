package com.j30n.stoblyx.infrastructure.security;

import com.j30n.stoblyx.common.util.XssUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * XSS 공격 방지를 위한 HttpServletRequest 래퍼 클래스
 * 요청 파라미터와 헤더, 본문에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] rawData;

    /**
     * XssRequestWrapper 생성자
     *
     * @param request 원본 HttpServletRequest
     * @throws IOException 입출력 예외 발생 시
     */
    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        // 요청 본문 읽기
        InputStream inputStream = request.getInputStream();
        this.rawData = StreamUtils.copyToByteArray(inputStream);
    }

    /**
     * 요청 파라미터 값에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
     *
     * @param paramName 파라미터 이름
     * @return 이스케이프 처리된 파라미터 값
     */
    @Override
    public String getParameter(String paramName) {
        String value = super.getParameter(paramName);
        return XssUtils.escape(value);
    }

    /**
     * 요청 파라미터 값 배열에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
     *
     * @param paramName 파라미터 이름
     * @return 이스케이프 처리된 파라미터 값 배열
     */
    @Override
    public String[] getParameterValues(String paramName) {
        String[] values = super.getParameterValues(paramName);
        if (values == null) {
            return new String[0];
        }

        String[] encodedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            encodedValues[i] = XssUtils.escape(values[i]);
        }

        return encodedValues;
    }

    /**
     * 요청 헤더 값에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
     *
     * @param name 헤더 이름
     * @return 이스케이프 처리된 헤더 값
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return XssUtils.escape(value);
    }

    /**
     * 요청 헤더 값 목록에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
     *
     * @param name 헤더 이름
     * @return 이스케이프 처리된 헤더 값 목록
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headerValues = super.getHeaders(name);
        if (headerValues == null) {
            return Collections.emptyEnumeration();
        }

        List<String> encodedValues = new ArrayList<>();
        while (headerValues.hasMoreElements()) {
            encodedValues.add(XssUtils.escape(headerValues.nextElement()));
        }

        return Collections.enumeration(encodedValues);
    }

    /**
     * 요청 본문에서 XSS 공격 가능성이 있는 문자를 이스케이프 처리합니다.
     *
     * @return 이스케이프 처리된 요청 본문 입력 스트림
     * @throws IOException 입출력 예외 발생 시
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 요청 본문을 문자열로 반환합니다.
     *
     * @return 요청 본문 문자열
     * @throws IOException 입출력 예외 발생 시
     */
    public String getRequestBody() throws IOException {
        return new String(this.rawData, StandardCharsets.UTF_8);
    }
} 