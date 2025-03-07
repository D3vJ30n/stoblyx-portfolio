package com.j30n.stoblyx.application.service.system;

import com.j30n.stoblyx.adapter.in.web.dto.system.SystemSettingDto;
import com.j30n.stoblyx.application.port.in.system.SystemSettingUseCase;
import com.j30n.stoblyx.domain.enums.SettingCategory;
import com.j30n.stoblyx.domain.model.SystemSetting;
import com.j30n.stoblyx.domain.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 시스템 설정 관리를 위한 서비스 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingService implements SystemSettingUseCase {

    private static final String KEY_NOT_FOUND_MSG = "존재하지 않는 설정 키입니다: ";
    private static final String SYSTEM_MANAGED_MSG = "시스템 관리 설정은 직접 수정할 수 없습니다: ";
    
    private final SystemSettingRepository systemSettingRepository;
    private final CacheManager cacheManager;

    /**
     * 모든 시스템 설정을 조회합니다.
     *
     * @return 시스템 설정 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingDto> getAllSettings() {
        return systemSettingRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * 특정 카테고리의 시스템 설정을 조회합니다.
     *
     * @param category 설정 카테고리
     * @return 해당 카테고리의 시스템 설정 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingDto> getSettingsByCategory(SettingCategory category) {
        return systemSettingRepository.findByCategory(category).stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * 설정 키로 시스템 설정을 조회합니다.
     *
     * @param key 설정 키
     * @return 시스템 설정 DTO Optional 객체
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SystemSettingDto> getSettingByKey(String key) {
        return systemSettingRepository.findByKey(key)
                .map(this::mapToDto);
    }

    /**
     * 새로운 시스템 설정을 생성합니다.
     *
     * @param settingDto 시스템 설정 DTO
     * @param adminId 관리자 ID
     * @return 생성된 시스템 설정 DTO
     */
    @Override
    @Transactional
    public SystemSettingDto createSetting(SystemSettingDto settingDto, Long adminId) {
        if (systemSettingRepository.existsByKey(settingDto.key())) {
            throw new IllegalArgumentException("이미 존재하는 설정 키입니다: " + settingDto.key());
        }

        SystemSetting setting = mapToEntity(settingDto);
        setting.setLastModifiedBy(adminId);
        
        SystemSetting savedSetting = systemSettingRepository.save(setting);
        return mapToDto(savedSetting);
    }

    /**
     * 기존 시스템 설정을 업데이트합니다.
     *
     * @param key 설정 키
     * @param settingDto 업데이트할 시스템 설정 DTO
     * @param adminId 관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    @Override
    @Transactional
    public SystemSettingDto updateSetting(String key, SystemSettingDto settingDto, Long adminId) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException(KEY_NOT_FOUND_MSG + key));

        // 시스템 관리 설정인 경우 수정 불가
        if (setting.isSystemManaged()) {
            throw new IllegalArgumentException(SYSTEM_MANAGED_MSG + key);
        }

        setting.setValue(settingDto.value());
        setting.setDescription(settingDto.description());
        setting.setCategory(settingDto.category());
        setting.setEncrypted(settingDto.encrypted());
        setting.setDefaultValue(settingDto.defaultValue());
        setting.setValidationPattern(settingDto.validationPattern());
        setting.setLastModifiedBy(adminId);

        SystemSetting updatedSetting = systemSettingRepository.save(setting);
        return mapToDto(updatedSetting);
    }

    /**
     * 시스템 설정을 삭제합니다.
     *
     * @param key 설정 키
     * @return 삭제 성공 여부
     */
    @Override
    @Transactional
    public boolean deleteSetting(String key) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException(KEY_NOT_FOUND_MSG + key));

        // 시스템 관리 설정인 경우 삭제 불가
        if (setting.isSystemManaged()) {
            throw new IllegalArgumentException(SYSTEM_MANAGED_MSG + key);
        }

        systemSettingRepository.delete(setting);
        return true;
    }

    /**
     * 키 패턴으로 시스템 설정을 검색합니다.
     *
     * @param keyPattern 키 패턴
     * @return 검색된 시스템 설정 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingDto> searchSettingsByKeyPattern(String keyPattern) {
        return systemSettingRepository.findByKeyPattern(keyPattern).stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * 시스템 설정을 기본값으로 초기화합니다.
     *
     * @param key 설정 키
     * @param adminId 관리자 ID
     * @return 초기화된 시스템 설정 DTO
     */
    @Override
    @Transactional
    public SystemSettingDto resetSettingToDefault(String key, Long adminId) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException(KEY_NOT_FOUND_MSG + key));

        if (setting.getDefaultValue() == null || setting.getDefaultValue().isEmpty()) {
            throw new IllegalArgumentException("기본값이 설정되지 않은 설정입니다: " + key);
        }

        setting.setValue(setting.getDefaultValue());
        setting.setLastModifiedBy(adminId);

        SystemSetting updatedSetting = systemSettingRepository.save(setting);
        return mapToDto(updatedSetting);
    }

    /**
     * Redis 캐시 TTL을 설정합니다.
     *
     * @param cacheName 캐시 이름
     * @param ttlSeconds TTL (초)
     * @param adminId 관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    @Override
    @Transactional
    public SystemSettingDto setCacheTTL(String cacheName, int ttlSeconds, Long adminId) {
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL은 0보다 커야 합니다.");
        }

        String key = "cache.ttl." + cacheName;
        Optional<SystemSetting> existingSetting = systemSettingRepository.findByKey(key);

        SystemSetting setting;
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setValue(String.valueOf(ttlSeconds));
            setting.setLastModifiedBy(adminId);
        } else {
            setting = SystemSetting.builder()
                    .key(key)
                    .value(String.valueOf(ttlSeconds))
                    .description(cacheName + " 캐시의 TTL (초)")
                    .category(SettingCategory.CACHE)
                    .encrypted(false)
                    .systemManaged(true)
                    .lastModifiedBy(adminId)
                    .defaultValue("3600") // 기본값 1시간
                    .build();
        }

        SystemSetting savedSetting = systemSettingRepository.save(setting);
        
        // 캐시 매니저를 통해 실제 캐시 TTL 설정 (실제 구현은 캐시 종류에 따라 다름)
        // 여기서는 예시로만 작성
        if (cacheManager.getCache(cacheName) != null) {
            // 캐시 TTL 설정 로직 (Redis 등 사용 시 구현)
        }
        
        return mapToDto(savedSetting);
    }

    /**
     * 랭킹 시스템 알고리즘 파라미터를 설정합니다.
     *
     * @param paramName 파라미터 이름
     * @param paramValue 파라미터 값
     * @param adminId 관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    @Override
    @Transactional
    public SystemSettingDto setRankingParameter(String paramName, String paramValue, Long adminId) {
        String key = "ranking.param." + paramName;
        Optional<SystemSetting> existingSetting = systemSettingRepository.findByKey(key);

        SystemSetting setting;
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setValue(paramValue);
            setting.setLastModifiedBy(adminId);
        } else {
            setting = SystemSetting.builder()
                    .key(key)
                    .value(paramValue)
                    .description("랭킹 시스템 파라미터: " + paramName)
                    .category(SettingCategory.RANKING)
                    .encrypted(false)
                    .systemManaged(true)
                    .lastModifiedBy(adminId)
                    .build();
        }

        SystemSetting savedSetting = systemSettingRepository.save(setting);
        return mapToDto(savedSetting);
    }

    /**
     * 게이미피케이션 랭크별 혜택을 설정합니다.
     *
     * @param rankName 랭크 이름
     * @param benefitJson 혜택 JSON 문자열
     * @param adminId 관리자 ID
     * @return 업데이트된 시스템 설정 DTO
     */
    @Override
    @Transactional
    public SystemSettingDto setRankBenefit(String rankName, String benefitJson, Long adminId) {
        String key = "gamification.rank.benefit." + rankName.toLowerCase();
        Optional<SystemSetting> existingSetting = systemSettingRepository.findByKey(key);

        SystemSetting setting;
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setValue(benefitJson);
            setting.setLastModifiedBy(adminId);
        } else {
            setting = SystemSetting.builder()
                    .key(key)
                    .value(benefitJson)
                    .description(rankName + " 랭크의 혜택 설정")
                    .category(SettingCategory.GAMIFICATION)
                    .encrypted(false)
                    .systemManaged(true)
                    .lastModifiedBy(adminId)
                    .build();
        }

        SystemSetting savedSetting = systemSettingRepository.save(setting);
        return mapToDto(savedSetting);
    }

    /**
     * 엔티티를 DTO로 변환합니다.
     *
     * @param entity 시스템 설정 엔티티
     * @return 시스템 설정 DTO
     */
    private SystemSettingDto mapToDto(SystemSetting entity) {
        return new SystemSettingDto(
                entity.getId(),
                entity.getKey(),
                entity.getValue(),
                entity.getDescription(),
                entity.getCategory(),
                entity.isEncrypted(),
                entity.isSystemManaged(),
                entity.getLastModifiedBy(),
                entity.getDefaultValue(),
                entity.getValidationPattern()
        );
    }

    /**
     * DTO를 엔티티로 변환합니다.
     *
     * @param dto 시스템 설정 DTO
     * @return 시스템 설정 엔티티
     */
    private SystemSetting mapToEntity(SystemSettingDto dto) {
        return SystemSetting.builder()
                .key(dto.key())
                .value(dto.value())
                .description(dto.description())
                .category(dto.category())
                .encrypted(dto.encrypted())
                .systemManaged(dto.systemManaged())
                .lastModifiedBy(dto.lastModifiedBy())
                .defaultValue(dto.defaultValue())
                .validationPattern(dto.validationPattern())
                .build();
    }
} 