package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Pexels API 클라이언트
 * 키워드 기반 이미지/비디오 검색을 담당합니다.
 */
@Slf4j
@Component
public class PexelsClient {
    private static final String PEXELS_API_URL_IMAGES = "https://api.pexels.com/v1/search";
    private static final String PEXELS_API_URL_VIDEOS = "https://api.pexels.com/videos/search";
    
    private static final String FALLBACK_IMAGE = "https://images.pexels.com/photos/3243/pen-calendar-to-do-checklist.jpg";
    private static final String FALLBACK_VIDEO = "https://www.pexels.com/video/open-book-854381/";
    private static final String DUMMY_API_KEY = "dummyKey";
    
    private static final String JSON_FIELD_PHOTOS = "photos";
    private static final String JSON_FIELD_VIDEOS = "videos";
    private static final String JSON_FIELD_SRC = "src";
    private static final String JSON_FIELD_ORIGINAL = "original";
    private static final String JSON_FIELD_URL = "url";
    private static final String JSON_FIELD_VIDEO_FILES = "video_files";
    private static final String JSON_FIELD_LINK = "link";
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final Random random;
    private final ObjectMapper objectMapper;
    
    // 최근 요청 시간 추적 (API 사용량 제한 대응)
    private long lastRequestTime = 0;
    
    // API 키를 application.properties에서 로드
    public PexelsClient(
            @Value("${pexels.api.key:}") String apiKey,  // 빈 기본값 사용
            RestTemplate restTemplate) {
        // 빈 값이면 DUMMY_API_KEY 사용
        this.apiKey = (apiKey == null || apiKey.isEmpty()) ? DUMMY_API_KEY : apiKey;
        this.restTemplate = restTemplate;
        this.random = new Random();
        this.objectMapper = new ObjectMapper();
        
        if (DUMMY_API_KEY.equals(this.apiKey)) {
            log.warn("Pexels API 키가 설정되지 않았습니다. 폴백 이미지/비디오가 사용됩니다.");
        }
    }
    
    /**
     * 키워드 기반 이미지 검색
     *
     * @param query 검색 키워드
     * @return 이미지 URL
     */
    @Cacheable(value = "pexelsImageCache", key = "#query")
    public String searchImage(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("검색어가 비어 있습니다. 폴백 이미지를 반환합니다.");
            return FALLBACK_IMAGE;
        }
        
        if (DUMMY_API_KEY.equals(apiKey)) {
            log.warn("API 키가 설정되지 않았습니다. 폴백 이미지를 반환합니다.");
            return FALLBACK_IMAGE;
        }
        
        try {
            // API 요청 속도 제한 (초당 요청 수 제한 대응)
            throttleRequest();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PEXELS_API_URL_IMAGES)
                    .queryParam("query", query)
                    .queryParam("per_page", 10)
                    .queryParam("orientation", "landscape"); // 가로 방향 이미지 (숏폼에 적합)
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                List<String> imageUrls = extractImageUrls(jsonResponse);
                
                if (!imageUrls.isEmpty()) {
                    // 랜덤하게 이미지 선택
                    return imageUrls.get(random.nextInt(imageUrls.size()));
                }
            }
            
            log.warn("이미지 검색 결과가 없거나 응답 형식이 올바르지 않습니다. 폴백 이미지를 반환합니다.");
            return FALLBACK_IMAGE;
            
        } catch (RestClientException e) {
            log.error("Pexels API 호출 중 오류가 발생했습니다: {}", e.getMessage());
            return FALLBACK_IMAGE;
        } catch (Exception e) {
            log.error("이미지 검색 중 오류 발생: {}", e.getMessage(), e);
            return FALLBACK_IMAGE;
        }
    }
    
    /**
     * 키워드 기반 비디오 검색
     *
     * @param query 검색 키워드
     * @return 비디오 URL
     */
    @Cacheable(value = "pexelsVideoCache", key = "#query")
    public String searchVideo(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("검색어가 비어 있습니다. 폴백 비디오를 반환합니다.");
            return FALLBACK_VIDEO;
        }
        
        if (DUMMY_API_KEY.equals(apiKey)) {
            log.warn("API 키가 설정되지 않았습니다. 폴백 비디오를 반환합니다.");
            return FALLBACK_VIDEO;
        }
        
        try {
            // API 요청 속도 제한
            throttleRequest();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PEXELS_API_URL_VIDEOS)
                    .queryParam("query", query)
                    .queryParam("per_page", 10)
                    .queryParam("orientation", "portrait"); // 세로 방향 비디오 (숏폼에 적합)
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                List<String> videoUrls = extractVideoUrls(jsonResponse);
                
                if (!videoUrls.isEmpty()) {
                    // 랜덤하게 비디오 선택
                    return videoUrls.get(random.nextInt(videoUrls.size()));
                }
            }
            
            log.warn("비디오 검색 결과가 없거나 응답 형식이 올바르지 않습니다. 폴백 비디오를 반환합니다.");
            return FALLBACK_VIDEO;
            
        } catch (RestClientException e) {
            log.error("Pexels API 호출 중 오류가 발생했습니다: {}", e.getMessage());
            return FALLBACK_VIDEO;
        } catch (Exception e) {
            log.error("비디오 검색 중 오류 발생: {}", e.getMessage(), e);
            return FALLBACK_VIDEO;
        }
    }
    
    /**
     * API 요청 속도 제한 (초당 요청 수 제한에 대응)
     */
    private synchronized void throttleRequest() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastRequestTime;
        
        // API 호출 간격을 최소 1초로 유지
        if (elapsedTime < 1000) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000 - elapsedTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        lastRequestTime = System.currentTimeMillis();
    }
    
    /**
     * 응답에서 이미지 URL 추출
     */
    private List<String> extractImageUrls(JsonNode response) {
        List<String> imageUrls = new ArrayList<>();
        
        if (response.has(JSON_FIELD_PHOTOS) && response.get(JSON_FIELD_PHOTOS).isArray()) {
            for (JsonNode photo : response.get(JSON_FIELD_PHOTOS)) {
                if (photo.has(JSON_FIELD_SRC) && photo.get(JSON_FIELD_SRC).has(JSON_FIELD_ORIGINAL)) {
                    imageUrls.add(photo.get(JSON_FIELD_SRC).get(JSON_FIELD_ORIGINAL).asText());
                }
            }
        }
        
        return imageUrls;
    }
    
    /**
     * 응답에서 비디오 URL 추출
     */
    private List<String> extractVideoUrls(JsonNode response) {
        List<String> videoUrls = new ArrayList<>();
        
        if (response.has(JSON_FIELD_VIDEOS) && response.get(JSON_FIELD_VIDEOS).isArray()) {
            for (JsonNode video : response.get(JSON_FIELD_VIDEOS)) {
                if (video.has(JSON_FIELD_URL)) {
                    videoUrls.add(video.get(JSON_FIELD_URL).asText());
                } else if (video.has(JSON_FIELD_VIDEO_FILES) && video.get(JSON_FIELD_VIDEO_FILES).isArray() && 
                           video.get(JSON_FIELD_VIDEO_FILES).size() > 0) {
                    videoUrls.add(video.get(JSON_FIELD_VIDEO_FILES).get(0).get(JSON_FIELD_LINK).asText());
                }
            }
        }
        
        return videoUrls;
    }
}
