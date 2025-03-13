package com.j30n.stoblyx.config;

import com.j30n.stoblyx.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트 환경에서 시스템 설정 정보를 제공하는 유틸리티 클래스
 * 이 클래스는 실제 컨트롤러가 아니라 테스트 도우미입니다.
 */
@TestConfiguration
@Profile("test")
public class SystemSettingTestController {
    
    /**
     * 시스템 설정 데이터를 생성합니다.
     * 테스트 시 필요한 곳에서 직접 호출하여 사용합니다.
     * 
     * @return 더미 시스템 설정 데이터
     */
    public static Map<String, Object> getDummySystemSettings() {
        Map<String, Object> dummySettings = new HashMap<>();
        dummySettings.put("app.name", "Stoblyx Portfolio");
        dummySettings.put("app.version", "1.0.0");
        dummySettings.put("app.mode", "test");
        dummySettings.put("feature.registration.enabled", true);
        dummySettings.put("feature.login.enabled", true);
        return dummySettings;
    }
    
    /**
     * 시스템 설정 API 응답을 생성합니다.
     * 
     * @return 시스템 설정 API 응답
     */
    public static ApiResponse<Map<String, Object>> getSystemSettingsResponse() {
        return new ApiResponse<>("SUCCESS", "시스템 설정 조회 성공", getDummySystemSettings());
    }
    
    /**
     * 시스템 설정 응답을 JSON 문자열로 변환합니다.
     * 
     * @return JSON 형식의 시스템 설정 응답
     */
    public static String getSystemSettingsResponseJson() {
        try {
            return new ObjectMapper().writeValueAsString(getSystemSettingsResponse());
        } catch (Exception e) {
            return "{\"result\":\"SUCCESS\",\"message\":\"시스템 설정 조회 성공\",\"data\":{\"app.name\":\"Stoblyx Portfolio\",\"app.version\":\"1.0.0\",\"app.mode\":\"test\",\"feature.registration.enabled\":true,\"feature.login.enabled\":true}}";
        }
    }
} 