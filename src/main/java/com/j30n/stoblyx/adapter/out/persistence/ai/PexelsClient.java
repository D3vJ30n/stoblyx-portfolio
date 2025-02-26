package com.j30n.stoblyx.adapter.out.persistence.ai;

import com.j30n.stoblyx.config.PexelsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Pexels API 클라이언트
 * 텍스트 기반 이미지/비디오 검색을 담당합니다.
 */
@Component
@RequiredArgsConstructor
public class PexelsClient {

    private final RestTemplate restTemplate;

    /**
     * 텍스트를 기반으로 이미지를 검색합니다.
     *
     * @param query 검색할 텍스트
     * @return 검색된 이미지의 URL
     */
    public String searchImage(String query) {
        String url = PexelsConfig.BASE_URL + "/search?query=" + query + "&per_page=1";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", PexelsConfig.API_KEY);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body != null && !((Map<String, Object>) body.get("photos")).isEmpty()) {
            Map<String, Object> photo = (Map<String, Object>) ((Map<String, Object>) body.get("photos")).get(0);
            Map<String, String> src = (Map<String, String>) photo.get("src");
            return src.get("original");
        }

        return null;
    }

    /**
     * 텍스트를 기반으로 비디오를 검색합니다.
     *
     * @param query 검색할 텍스트
     * @return 검색된 비디오의 URL
     */
    public String searchVideo(String query) {
        String url = PexelsConfig.BASE_URL + "/videos/search?query=" + query + "&per_page=1";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", PexelsConfig.API_KEY);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body != null && !((Map<String, Object>) body.get("videos")).isEmpty()) {
            Map<String, Object> video = (Map<String, Object>) ((Map<String, Object>) body.get("videos")).get(0);
            Map<String, String> files = (Map<String, String>) video.get("video_files");
            return files.get("link");
        }

        return null;
    }
}
