package com.j30n.stoblyx.application.service.system;

import com.j30n.stoblyx.adapter.in.web.dto.system.SystemSettingDto;
import com.j30n.stoblyx.application.port.in.system.SystemSettingUseCase;
import com.j30n.stoblyx.domain.enums.SettingCategory;
import com.j30n.stoblyx.domain.model.SystemSetting;
import com.j30n.stoblyx.domain.repository.SystemSettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 시스템 설정 관리를 위한 서비스 구현 클래스
 */
@Slf4j
@Service
public class SystemSettingService implements SystemSettingUseCase {

    private static final String KEY_NOT_FOUND_MSG = "존재하지 않는 설정 키입니다: ";
    private static final String SYSTEM_MANAGED_MSG = "시스템 관리 설정은 직접 수정할 수 없습니다: ";
    private static final String CATEGORY_KEY = "category";
    private static final String SETTINGS_KEY = "settings";
    
    private final SystemSettingRepository systemSettingRepository;
    private final CacheManager cacheManager;
    
    private final SystemSettingService self;
    
    // 유효한 캐시 이름 목록
    private static final List<String> VALID_CACHE_NAMES = Arrays.asList(
        "userCache", "contentCache", "settingCache", "quotesCache"
    );
    
    // 유효한 랭킹 파라미터 이름 목록
    private static final List<String> VALID_RANKING_PARAMS = Arrays.asList(
        "score.weight.activity", "score.weight.engagement", 
        "score.weight.content", "score.weight.retention",
        "threshold.promotion", "threshold.demotion"
    );
    
    // 유효한 랭크 이름 목록
    private static final List<String> VALID_RANK_NAMES = Arrays.asList(
        "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND"
    );

    public SystemSettingService(
            SystemSettingRepository systemSettingRepository,
            CacheManager cacheManager,
            @Lazy SystemSettingService self) {
        this.systemSettingRepository = systemSettingRepository;
        this.cacheManager = cacheManager;
        this.self = self;
    }

    /**
     * 주어진 설정 키가 시스템 중요 설정인지 확인합니다.
     *
     * @param key 설정 키
     * @return 시스템 중요 설정 여부
     */
    public boolean isSystemCriticalSetting(String key) {
        // 시스템 중요 설정 키 패턴 확인
        return key.startsWith("system.") || 
               key.startsWith("security.") || 
               key.startsWith("core.");
    }
    
    /**
     * 캐시 이름이 유효한지 확인합니다.
     *
     * @param cacheName 캐시 이름
     * @return 유효성 여부
     */
    public boolean isCacheNameValid(String cacheName) {
        // 캐시 매니저에서 캐시 존재 여부 확인
        if (cacheManager.getCache(cacheName) != null) {
            return true;
        }
        
        // 미리 정의된 유효한 캐시 이름 목록에서 확인
        return VALID_CACHE_NAMES.contains(cacheName);
    }
    
    /**
     * 랭킹 파라미터 이름이 유효한지 확인합니다.
     *
     * @param paramName 파라미터 이름
     * @return 유효성 여부
     */
    public boolean isValidRankingParameter(String paramName) {
        return VALID_RANKING_PARAMS.contains(paramName);
    }
    
    /**
     * 랭킹 파라미터 값이 유효한지 확인합니다.
     *
     * @param paramName  파라미터 이름
     * @param paramValue 파라미터 값
     * @return 유효성 여부
     */
    public boolean isValidRankingParameterValue(String paramName, String paramValue) {
        if (paramName.startsWith("score.weight.")) {
            // 가중치 값은 0.0-1.0 사이의 소수여야 함
            try {
                double weight = Double.parseDouble(paramValue);
                return weight >= 0.0 && weight <= 1.0;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (paramName.startsWith("threshold.")) {
            // 임계값은 양의 정수여야 함
            try {
                int threshold = Integer.parseInt(paramValue);
                return threshold > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // 알 수 없는 파라미터 타입
        return false;
    }
    
    /**
     * 랭크 이름이 유효한지 확인합니다.
     *
     * @param rankName 랭크 이름
     * @return 유효성 여부
     */
    public boolean isValidRankName(String rankName) {
        return VALID_RANK_NAMES.contains(rankName);
    }
    
    /**
     * 혜택 JSON이 유효한지 확인합니다.
     *
     * @param benefitJson 혜택 JSON
     * @return 유효성 여부
     */
    public boolean isValidBenefitJson(String benefitJson) {
        if (benefitJson == null || benefitJson.trim().isEmpty()) {
            return false;
        }
        
        // 기본적인 JSON 형식 검증
        if (!benefitJson.startsWith("{") || !benefitJson.endsWith("}")) {
            return false;
        }
        
        try {
            // JSON 파싱 시도 (실제 구현에서는 Jackson 등의 라이브러리 사용)
            // 여기서는 간단한 검증만 수행
            return true;
        } catch (Exception e) {
            log.warn("혜택 JSON 파싱 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 설정을 일괄 업데이트합니다.
     *
     * @param settings 업데이트할 설정 맵 (키-값 쌍)
     * @param adminId  관리자 ID
     * @return 업데이트된 설정 목록
     */
    @Transactional
    public List<SystemSettingDto> batchUpdateSettings(Map<String, String> settings, Long adminId) {
        List<SystemSettingDto> updatedSettings = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // 기존 설정 조회
            Optional<SystemSettingDto> existingSettingOpt = self.getSettingByKey(key);
            
            if (existingSettingOpt.isPresent()) {
                // 기존 설정 업데이트
                SystemSettingDto existingSetting = existingSettingOpt.get();
                
                // 시스템 관리 설정은 수정 불가
                if (existingSetting.systemManaged()) {
                    log.warn("시스템 관리 설정은 일괄 업데이트에서 제외됩니다: {}", key);
                    continue;
                }
                
                // 새 DTO 생성 (record는 불변이므로 새 인스턴스 생성)
                SystemSettingDto updatedDto = new SystemSettingDto(
                    existingSetting.id(),
                    existingSetting.key(),
                    value, // 새 값으로 업데이트
                    existingSetting.description(),
                    existingSetting.category(),
                    existingSetting.encrypted(),
                    existingSetting.systemManaged(),
                    adminId, // 마지막 수정자 업데이트
                    existingSetting.defaultValue(),
                    existingSetting.validationPattern()
                );
                
                // 업데이트 수행
                SystemSettingDto updatedSetting = self.updateSetting(key, updatedDto, adminId);
                updatedSettings.add(updatedSetting);
            } else {
                // 새 설정 생성 (카테고리는 기본값으로 GENERAL 사용)
                SystemSettingDto newSetting = new SystemSettingDto(
                    null, // ID는 저장 시 생성됨
                    key,
                    value,
                    "일괄 생성된 설정",
                    SettingCategory.GENERAL,
                    false, // 암호화 안 함
                    false, // 시스템 관리 아님
                    adminId,
                    null, // 기본값 없음
                    null  // 유효성 검사 패턴 없음
                );
                
                // 생성 수행
                SystemSettingDto createdSetting = self.createSetting(newSetting, adminId);
                updatedSettings.add(createdSetting);
            }
        }
        
        return updatedSettings;
    }
    
    /**
     * 설정을 내보냅니다.
     *
     * @param category 카테고리 필터 (선택사항)
     * @return 내보낸 설정 데이터
     */
    @Transactional(readOnly = true)
    public Map<String, Object> exportSettings(SettingCategory category) {
        Map<String, Object> exportData = new HashMap<>();
        List<SystemSettingDto> settings;
        
        if (category != null) {
            settings = self.getSettingsByCategory(category);
            exportData.put(CATEGORY_KEY, category.name());
        } else {
            settings = self.getAllSettings();
            exportData.put(CATEGORY_KEY, "ALL");
        }
        
        exportData.put("exportDate", java.time.LocalDateTime.now().toString());
        exportData.put("count", settings.size());
        
        Map<String, Object> settingsMap = new HashMap<>();
        for (SystemSettingDto setting : settings) {
            // 민감한 설정은 내보내기에서 제외 (선택적)
            if (setting.encrypted()) {
                continue;
            }
            
            settingsMap.put(setting.key(), setting);
        }
        exportData.put(SETTINGS_KEY, settingsMap);
        
        return exportData;
    }
    
    /**
     * 설정을 가져옵니다.
     *
     * @param settings  가져올 설정 데이터
     * @param overwrite 기존 설정 덮어쓰기 여부
     * @param adminId   관리자 ID
     * @return 가져온 설정 목록
     */
    @Transactional
    public List<SystemSettingDto> importSettings(Map<String, Object> settings, boolean overwrite, Long adminId) {
        List<SystemSettingDto> importedSettings = new ArrayList<>();
        
        if (!settings.containsKey(SETTINGS_KEY)) {
            throw new IllegalArgumentException("유효하지 않은 설정 데이터 형식입니다.");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> settingsMap = (Map<String, Object>) settings.get(SETTINGS_KEY);
        
        for (Map.Entry<String, Object> entry : settingsMap.entrySet()) {
            String key = entry.getKey();
            
            // 기존 설정 확인
            Optional<SystemSettingDto> existingSettingOpt = self.getSettingByKey(key);
            
            if (existingSettingOpt.isPresent() && !overwrite) {
                // 덮어쓰기 옵션이 꺼져 있을 때 기존 설정 건너뛰기
                log.info("설정 가져오기: 기존 설정 건너뛰기 - {}", key);
                continue;
            }
            
            // 시스템 관리 설정은 가져오기에서 제외
            if (existingSettingOpt.isPresent() && existingSettingOpt.get().systemManaged()) {
                log.warn("시스템 관리 설정은 가져오기에서 제외됩니다: {}", key);
                continue;
            }
            
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> settingMap = (Map<String, Object>) entry.getValue();
                
                String value = settingMap.get("value").toString();
                SettingCategory category = SettingCategory.valueOf(settingMap.get(CATEGORY_KEY).toString());
                String description = settingMap.getOrDefault("description", "").toString();
                
                // 기존 설정이 있으면 업데이트, 없으면 생성
                if (existingSettingOpt.isPresent()) {
                    SystemSettingDto existingSetting = existingSettingOpt.get();
                    
                    // 새 DTO 생성
                    SystemSettingDto updatedDto = new SystemSettingDto(
                        existingSetting.id(),
                        key,
                        value,
                        description,
                        category,
                        existingSetting.encrypted(),
                        existingSetting.systemManaged(),
                        adminId,
                        existingSetting.defaultValue(),
                        existingSetting.validationPattern()
                    );
                    
                    // 업데이트 수행
                    SystemSettingDto updatedSetting = self.updateSetting(key, updatedDto, adminId);
                    importedSettings.add(updatedSetting);
                } else {
                    // 새 설정 생성
                    SystemSettingDto newSetting = new SystemSettingDto(
                        null,
                        key,
                        value,
                        description,
                        category,
                        false,
                        false,
                        adminId,
                        null,
                        null
                    );
                    
                    // 생성 수행
                    SystemSettingDto createdSetting = self.createSetting(newSetting, adminId);
                    importedSettings.add(createdSetting);
                }
            } catch (Exception e) {
                log.warn("설정 가져오기 중 오류 발생: {} - {}", key, e.getMessage());
                // 오류가 발생해도 계속 진행
            }
        }
        
        return importedSettings;
    }

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