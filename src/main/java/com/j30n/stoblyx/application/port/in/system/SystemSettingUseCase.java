package com.j30n.stoblyx.application.port.in.system;

import com.j30n.stoblyx.adapter.in.web.dto.system.SystemSettingDto;
import com.j30n.stoblyx.domain.enums.SettingCategory;

import java.util.List;
import java.util.Optional;

/**
 * 시스템 설정 관리를 위한 유스케이스 인터페이스
 */
public interface SystemSettingUseCase {

    /**
     * 모든 시스템 설정을 조회합니다.
     *
     * @return 시스템 설정 DTO 목록
     */
    List<SystemSettingDto> getAllSettings();

    /**
     * 특정 카테고리의 시스템 설정을 조회합니다.
     *
     * @param category 설정 카테고리
     * @return 해당 카테고리의 시스템 설정 DTO 목록
     */
    List<SystemSettingDto> getSettingsByCategory(SettingCategory category);

    /**
     * 설정 키로 시스템 설정을 조회합니다.
     *
     * @param key 설정 키
     * @return 시스템 설정 DTO Optional 객체
     */
    Optional<SystemSettingDto> getSettingByKey(String key);

    /**
     * 새로운 시스템 설정을 생성합니다.
     *
     * @param settingDto 시스템 설정 DTO
     * @param adminId    관리자 ID
     * @return 생성된 시스템 설정 DTO
     */
    SystemSettingDto createSetting(SystemSettingDto settingDto, Long adminId);

    /**
     * 기존 시스템 설정을 업데이트합니다.
     *
     * @param key        설정 키
     * @param settingDto 업데이트할 시스템 설정 DTO
     * @param adminId    관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    SystemSettingDto updateSetting(String key, SystemSettingDto settingDto, Long adminId);

    /**
     * 시스템 설정을 삭제합니다.
     *
     * @param key 설정 키
     * @return 삭제 성공 여부
     */
    boolean deleteSetting(String key);

    /**
     * 키 패턴으로 시스템 설정을 검색합니다.
     *
     * @param keyPattern 키 패턴
     * @return 검색된 시스템 설정 DTO 목록
     */
    List<SystemSettingDto> searchSettingsByKeyPattern(String keyPattern);

    /**
     * 시스템 설정을 기본값으로 초기화합니다.
     *
     * @param key     설정 키
     * @param adminId 관리자 ID
     * @return 초기화된 시스템 설정 DTO
     */
    SystemSettingDto resetSettingToDefault(String key, Long adminId);

    /**
     * Redis 캐시 TTL을 설정합니다.
     *
     * @param cacheName  캐시 이름
     * @param ttlSeconds TTL (초)
     * @param adminId    관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    SystemSettingDto setCacheTTL(String cacheName, int ttlSeconds, Long adminId);

    /**
     * 랭킹 시스템 알고리즘 파라미터를 설정합니다.
     *
     * @param paramName  파라미터 이름
     * @param paramValue 파라미터 값
     * @param adminId    관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    SystemSettingDto setRankingParameter(String paramName, String paramValue, Long adminId);

    /**
     * 게이미피케이션 랭크별 혜택을 설정합니다.
     *
     * @param rankName    랭크 이름
     * @param benefitJson 혜택 JSON 문자열
     * @param adminId     관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    SystemSettingDto setRankBenefit(String rankName, String benefitJson, Long adminId);
} 