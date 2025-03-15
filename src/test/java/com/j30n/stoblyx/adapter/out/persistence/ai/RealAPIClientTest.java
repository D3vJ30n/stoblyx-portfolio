package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 실제 API 호출 테스트
 * 실제 API 키를 사용하여 Pexels API가 정상 작동하는지 확인하는 테스트
 */
@DisplayName("실제 API 호출 테스트")
class RealAPIClientTest {

    private static final String API_KEY = "Yyicakz4WGEu9XntPLqVIR4JUKEAokSMG7FfoAc35m6kHhnJnu5kHkPa";
    
    @Test
    @DisplayName("실제 Pexels API 이미지 검색 테스트")
    void realPexelsImageSearchTest() {
        // 실제 API 호출을 위한 클라이언트 생성
        RestTemplate realRestTemplate = new RestTemplate();
        
        // Authorization 헤더를 추가하는 인터셉터
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, API_KEY);
            return execution.execute(request, body);
        };
        
        realRestTemplate.setInterceptors(Collections.singletonList(interceptor));
        
        PexelsClient pexelsClient = new PexelsClient(
            API_KEY,
            realRestTemplate,
            new Random(),
            new ObjectMapper());

        // 검색 쿼리
        String query = "books library reading";

        // 실제 API 호출
        String imageUrl = pexelsClient.searchImage(query);

        // 결과 검증
        assertNotNull(imageUrl);
        System.out.println("실제 이미지 URL: " + imageUrl);
        // URL 형식 검증
        assertThat(imageUrl).contains("https://");
        assertThat(imageUrl).contains("pexels.com");
    }

    @Test
    @DisplayName("실제 Pexels API 비디오 검색 테스트")
    void realPexelsVideoSearchTest() {
        // 실제 API 호출을 위한 클라이언트 생성
        RestTemplate realRestTemplate = new RestTemplate();
        
        // Authorization 헤더를 추가하는 인터셉터
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, API_KEY);
            return execution.execute(request, body);
        };
        
        realRestTemplate.setInterceptors(Collections.singletonList(interceptor));
        
        PexelsClient pexelsClient = new PexelsClient(
            API_KEY,
            realRestTemplate,
            new Random(),
            new ObjectMapper());

        // 검색 쿼리
        String query = "books reading library";

        // 실제 API 호출
        String videoUrl = pexelsClient.searchVideo(query);

        // 결과 검증
        assertNotNull(videoUrl);
        System.out.println("실제 비디오 URL: " + videoUrl);
        // URL 형식 검증
        assertThat(videoUrl).contains("https://");
        assertThat(videoUrl).contains("pexels.com");
    }
} 