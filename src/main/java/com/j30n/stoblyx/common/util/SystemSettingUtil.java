package com.j30n.stoblyx.common.util;

import com.j30n.stoblyx.application.port.in.system.SystemSettingUseCase;
import org.springframework.stereotype.Component;

/**
 * 시스템 설정 값을 쉽게 가져오기 위한 유틸리티 클래스
 */
@Component
public class SystemSettingUtil {

    private static SystemSettingUseCase systemSettingUseCase;

    public SystemSettingUtil(SystemSettingUseCase systemSettingUseCase) {
        SystemSettingUtil.systemSettingUseCase = systemSettingUseCase;
    }

    /**
     * 문자열 설정 값을 가져옵니다.
     *
     * @param key          설정 키
     * @param defaultValue 기본값 (설정이 없을 경우)
     * @return 설정 값 또는 기본값
     */
    public static String getString(String key, String defaultValue) {
        return systemSettingUseCase.getSettingByKey(key)
            .map(dto -> dto.value())
            .orElse(defaultValue);
    }

    /**
     * 정수 설정 값을 가져옵니다.
     *
     * @param key          설정 키
     * @param defaultValue 기본값 (설정이 없을 경우)
     * @return 설정 값 또는 기본값
     */
    public static int getInt(String key, int defaultValue) {
        return systemSettingUseCase.getSettingByKey(key)
            .map(dto -> {
                try {
                    return Integer.parseInt(dto.value());
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            })
            .orElse(defaultValue);
    }

    /**
     * 실수 설정 값을 가져옵니다.
     *
     * @param key          설정 키
     * @param defaultValue 기본값 (설정이 없을 경우)
     * @return 설정 값 또는 기본값
     */
    public static double getDouble(String key, double defaultValue) {
        return systemSettingUseCase.getSettingByKey(key)
            .map(dto -> {
                try {
                    return Double.parseDouble(dto.value());
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            })
            .orElse(defaultValue);
    }

    /**
     * 불리언 설정 값을 가져옵니다.
     *
     * @param key          설정 키
     * @param defaultValue 기본값 (설정이 없을 경우)
     * @return 설정 값 또는 기본값
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return systemSettingUseCase.getSettingByKey(key)
            .map(dto -> Boolean.parseBoolean(dto.value()))
            .orElse(defaultValue);
    }

    /**
     * API 키를 가져옵니다.
     *
     * @param apiName API 이름
     * @return API 키
     */
    public static String getApiKey(String apiName) {
        return getString("api.key." + apiName, "");
    }

    /**
     * 리소스 경로를 가져옵니다.
     *
     * @param resourceType 리소스 유형
     * @return 리소스 경로
     */
    public static String getResourcePath(String resourceType) {
        return getString("resource.path." + resourceType, "./");
    }

    /**
     * 캐시 TTL을 가져옵니다.
     *
     * @param cacheName 캐시 이름
     * @return 캐시 TTL (초)
     */
    public static int getCacheTTL(String cacheName) {
        return getInt("cache.ttl." + cacheName, 3600);
    }

    /**
     * 랭킹 시스템 파라미터를 가져옵니다.
     *
     * @param paramName    파라미터 이름
     * @param defaultValue 기본값
     * @return 파라미터 값
     */
    public static double getRankingParameter(String paramName, double defaultValue) {
        return getDouble("ranking.param." + paramName, defaultValue);
    }

    /**
     * 게이미피케이션 랭크 임계값을 가져옵니다.
     *
     * @param rankName 랭크 이름
     * @return 랭크 임계값
     */
    public static int getRankThreshold(String rankName) {
        return getInt("gamification.rank.threshold." + rankName.toLowerCase(), 0);
    }

    /**
     * 게이미피케이션 랭크 혜택을 가져옵니다.
     *
     * @param rankName 랭크 이름
     * @return 랭크 혜택 JSON 문자열
     */
    public static String getRankBenefit(String rankName) {
        return getString("gamification.rank.benefit." + rankName.toLowerCase(), "{}");
    }
} 