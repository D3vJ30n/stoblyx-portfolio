package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.system.SystemSettingDto;
import com.j30n.stoblyx.application.service.system.SystemSettingService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.enums.SettingCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 시스템 설정 관리를 위한 REST 컨트롤러
 * 관리자 권한이 있는 사용자만 접근할 수 있습니다.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    /**
     * 현재 인증된 사용자의 ID를 가져옵니다.
     *
     * @return 현재 인증된 사용자의 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        // SecurityContext에서 인증된 사용자 정보 추출
        Object principal = authentication.getPrincipal();
        
        // UserDetails 타입인 경우 (일반적인 폼 로그인)
        if (principal instanceof UserDetails) {
            // UserDetails의 username은 보통 DB의 PK나 unique 식별자일 경우가 많음
            // 프로젝트의 인증 방식에 따라 적절히 변환
            try {
                return Long.parseLong(((UserDetails) principal).getUsername());
            } catch (NumberFormatException e) {
                log.warn("사용자 ID를 숫자로 변환할 수 없습니다: {}", ((UserDetails) principal).getUsername());
            }
        }
        
        // JWT 토큰 인증의 경우 종종 토큰 내 클레임에 userId가 포함됨
        if (authentication.getCredentials() instanceof Map) {
            Map<String, Object> credentials = (Map<String, Object>) authentication.getCredentials();
            if (credentials.containsKey("userId")) {
                return Long.valueOf(credentials.get("userId").toString());
            }
        }
        
        // 최종적으로 인증 객체에서 ID를 추출하지 못한 경우
        log.error("인증된 사용자의 ID를 추출할 수 없습니다.");
        throw new IllegalStateException("인증된 사용자의 ID를 추출할 수 없습니다.");
    }

    /**
     * 모든 시스템 설정을 조회합니다.
     *
     * @return 시스템 설정 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> getAllSettings() {
        try {
            log.info("모든 시스템 설정 조회 요청 처리");
            List<SystemSettingDto> settings = systemSettingService.getAllSettings();
            return ResponseEntity.ok(
                    new ApiResponse<>("SUCCESS", "시스템 설정 조회 성공", settings)
            );
        } catch (Exception e) {
            log.error("시스템 설정 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 카테고리별 시스템 설정을 조회합니다.
     *
     * @param category 설정 카테고리
     * @return 해당 카테고리의 시스템 설정 목록
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> getSettingsByCategory(
            @PathVariable SettingCategory category) {
        try {
            log.info("카테고리 {} 설정 조회 요청 처리", category);
            List<SystemSettingDto> settings = systemSettingService.getSettingsByCategory(category);
            String message = String.format("%s 카테고리 설정 조회 성공", category);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", message, settings));
        } catch (Exception e) {
            log.error("카테고리별 설정 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 키로 시스템 설정을 조회합니다.
     *
     * @param key 설정 키
     * @return 해당 키의 시스템 설정
     */
    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<SystemSettingDto>> getSettingByKey(@PathVariable String key) {
        try {
            log.info("키 '{}' 설정 조회 요청 처리", key);
            return systemSettingService.getSettingByKey(key)
                .map(setting -> ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 조회 성공", setting)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("ERROR", "존재하지 않는 설정 키입니다: " + key, null)));
        } catch (Exception e) {
            log.error("설정 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 새로운 시스템 설정을 생성합니다.
     *
     * @param settingDto 생성할 설정 정보
     * @return 생성된 설정 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SystemSettingDto>> createSetting(@Valid @RequestBody SystemSettingDto settingDto) {
        try {
            log.info("설정 생성 요청 처리: {}", settingDto);
            
            Long adminId = getCurrentUserId();
            
            // 이미 존재하는 키인지 확인
            if (systemSettingService.getSettingByKey(settingDto.key()).isPresent()) {
                log.warn("설정 생성 실패: 이미 존재하는 키 {}", settingDto.key());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>("ERROR", "이미 존재하는 설정 키입니다.", null));
            }
            
            SystemSettingDto createdSetting = systemSettingService.createSetting(settingDto, adminId);
            log.info("설정 생성 성공: {}", createdSetting);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("SUCCESS", "설정 생성 성공", createdSetting));
        } catch (IllegalArgumentException e) {
            log.warn("설정 생성 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("설정 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 기존 시스템 설정을 수정합니다.
     *
     * @param key        수정할 설정 키
     * @param settingDto 수정할 설정 정보
     * @return 수정된 설정 정보
     */
    @PutMapping("/{key}")
    public ResponseEntity<ApiResponse<SystemSettingDto>> updateSetting(
            @PathVariable String key, @Valid @RequestBody SystemSettingDto settingDto) {
        try {
            log.info("설정 수정 요청 처리: 키={}", key);
            
            Long adminId = getCurrentUserId();
            
            // 키 일치 여부 확인
            if (!key.equals(settingDto.key())) {
                log.warn("설정 수정 실패: 경로 변수 키({})와 요청 본문 키({})가 일치하지 않음", key, settingDto.key());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "경로 변수의 키와 요청 본문의 키가 일치하지 않습니다.", null));
            }
            
            // 존재하는 설정인지 확인
            if (!systemSettingService.getSettingByKey(key).isPresent()) {
                log.warn("설정 수정 실패: 존재하지 않는 키 {}", key);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("ERROR", "존재하지 않는 설정 키입니다: " + key, null));
            }
            
            SystemSettingDto updatedSetting = systemSettingService.updateSetting(key, settingDto, adminId);
            log.info("설정 수정 성공: {}", key);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 수정 성공", updatedSetting));
        } catch (IllegalArgumentException e) {
            log.warn("설정 수정 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("설정 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 기존 시스템 설정을 삭제합니다.
     *
     * @param key 삭제할 설정 키
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse<Boolean>> deleteSetting(@PathVariable String key) {
        try {
            log.info("설정 삭제 요청 처리: 키={}", key);
            
            // 존재하는 설정인지 확인
            if (!systemSettingService.getSettingByKey(key).isPresent()) {
                log.warn("설정 삭제 실패: 존재하지 않는 키 {}", key);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("ERROR", "존재하지 않는 설정 키입니다: " + key, null));
            }
            
            // 시스템 필수 설정은 삭제 불가능하도록 처리
            if (systemSettingService.isSystemCriticalSetting(key)) {
                log.warn("설정 삭제 실패: 보호된 시스템 설정 키 {}", key);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>("ERROR", "이 설정은 시스템에 필수적이므로 삭제할 수 없습니다.", false));
            }
            
            boolean result = systemSettingService.deleteSetting(key);
            log.info("설정 삭제 성공: {}", key);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 삭제 성공", result));
        } catch (IllegalArgumentException e) {
            log.warn("설정 삭제 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("설정 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 키 패턴으로 설정을 검색합니다.
     *
     * @param keyPattern 검색할 키 패턴
     * @return 검색된 설정 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> searchSettingsByKeyPattern(
            @RequestParam String keyPattern) {
        try {
            log.info("설정 검색 요청 처리: 패턴={}", keyPattern);
            
            if (keyPattern == null || keyPattern.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "검색 패턴은 비어있을 수 없습니다.", null));
            }
            
            List<SystemSettingDto> settings = systemSettingService.searchSettingsByKeyPattern(keyPattern);
            log.info("설정 검색 성공: 패턴={}, 결과 수={}", keyPattern, settings.size());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 검색 성공", settings));
        } catch (Exception e) {
            log.error("설정 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 설정을 기본값으로 초기화합니다.
     *
     * @param key 초기화할 설정 키
     * @return 초기화된 설정 정보
     */
    @PostMapping("/{key}/reset")
    public ResponseEntity<ApiResponse<SystemSettingDto>> resetSettingToDefault(@PathVariable String key) {
        try {
            log.info("설정 초기화 요청 처리: 키={}", key);
            
            Long adminId = getCurrentUserId();
            
            // 존재하는 설정인지 확인
            if (!systemSettingService.getSettingByKey(key).isPresent()) {
                log.warn("설정 초기화 실패: 존재하지 않는 키 {}", key);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("ERROR", "존재하지 않는 설정 키입니다: " + key, null));
            }
            
            SystemSettingDto resetSetting = systemSettingService.resetSettingToDefault(key, adminId);
            log.info("설정 초기화 성공: {}", key);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 초기화 성공", resetSetting));
        } catch (IllegalArgumentException e) {
            log.warn("설정 초기화 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("설정 초기화 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 캐시 TTL(Time To Live)을 설정합니다.
     *
     * @param cacheName  캐시 이름
     * @param ttlSeconds TTL 초 단위
     * @return 업데이트된 설정 정보
     */
    @PostMapping("/cache/{cacheName}/ttl")
    public ResponseEntity<ApiResponse<SystemSettingDto>> setCacheTTL(
            @PathVariable String cacheName, @RequestParam int ttlSeconds) {
        try {
            log.info("캐시 TTL 설정 요청 처리: 캐시={}, TTL={}초", cacheName, ttlSeconds);
            
            Long adminId = getCurrentUserId();
            
            if (ttlSeconds < 0) {
                log.warn("캐시 TTL 설정 실패: 음수 TTL 값 {}", ttlSeconds);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "TTL 값은 0 이상이어야 합니다.", null));
            }
            
            // 유효한 캐시인지 확인
            if (!systemSettingService.isCacheNameValid(cacheName)) {
                log.warn("캐시 TTL 설정 실패: 존재하지 않는 캐시 {}", cacheName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("ERROR", "지정된 캐시를 찾을 수 없습니다.", null));
            }
            
            SystemSettingDto cacheSetting = systemSettingService.setCacheTTL(cacheName, ttlSeconds, adminId);
            log.info("캐시 TTL 설정 성공: 캐시={}, TTL={}초", cacheName, ttlSeconds);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "캐시 TTL 설정 성공", cacheSetting));
        } catch (IllegalArgumentException e) {
            log.warn("캐시 TTL 설정 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("캐시 TTL 설정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 랭킹 시스템 매개변수를 설정합니다.
     *
     * @param paramName  매개변수 이름
     * @param paramValue 매개변수 값
     * @return 업데이트된 설정 정보
     */
    @PostMapping("/ranking/param")
    public ResponseEntity<ApiResponse<SystemSettingDto>> setRankingParameter(
            @RequestParam String paramName, @RequestParam String paramValue) {
        try {
            log.info("랭킹 파라미터 설정 요청 처리: 파라미터={}, 값={}", paramName, paramValue);
            
            Long adminId = getCurrentUserId();
            
            // 유효한 랭킹 파라미터인지 검증
            if (!systemSettingService.isValidRankingParameter(paramName)) {
                log.warn("랭킹 파라미터 설정 실패: 지원되지 않는 파라미터 {}", paramName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "지원되지 않는 랭킹 파라미터입니다.", null));
            }
            
            // 파라미터 값 유효성 검증
            if (!systemSettingService.isValidRankingParameterValue(paramName, paramValue)) {
                log.warn("랭킹 파라미터 설정 실패: 유효하지 않은 값 {}", paramValue);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "유효하지 않은 파라미터 값입니다.", null));
            }
            
            SystemSettingDto rankingSetting = systemSettingService.setRankingParameter(paramName, paramValue, adminId);
            log.info("랭킹 파라미터 설정 성공: 파라미터={}, 값={}", paramName, paramValue);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "랭킹 파라미터 설정 성공", rankingSetting));
        } catch (IllegalArgumentException e) {
            log.warn("랭킹 파라미터 설정 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("랭킹 파라미터 설정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 랭크별 혜택을 설정합니다.
     *
     * @param rankName    랭크 이름
     * @param benefitJson 혜택 JSON 문자열
     * @return 업데이트된 설정 정보
     */
    @PostMapping("/gamification/rank/benefit")
    public ResponseEntity<ApiResponse<SystemSettingDto>> setRankBenefit(
            @RequestParam String rankName, @RequestBody String benefitJson) {
        try {
            log.info("랭크 혜택 설정 요청 처리: 랭크={}", rankName);
            
            Long adminId = getCurrentUserId();
            
            // 유효한 랭크인지 검증
            if (!systemSettingService.isValidRankName(rankName)) {
                log.warn("랭크 혜택 설정 실패: 유효하지 않은 랭크 {}", rankName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "유효하지 않은 랭크 이름입니다.", null));
            }
            
            // JSON 형식 검증
            if (!systemSettingService.isValidBenefitJson(benefitJson)) {
                log.warn("랭크 혜택 설정 실패: 유효하지 않은 JSON 형식");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "유효하지 않은 혜택 JSON 형식입니다.", null));
            }
            
            SystemSettingDto benefitSetting = systemSettingService.setRankBenefit(rankName, benefitJson, adminId);
            log.info("랭크 혜택 설정 성공: 랭크={}", rankName);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "랭크 혜택 설정 성공", benefitSetting));
        } catch (IllegalArgumentException e) {
            log.warn("랭크 혜택 설정 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("랭크 혜택 설정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
    
    /**
     * 시스템 설정 일괄 업데이트
     * 
     * @param settings 업데이트할 설정 맵 (키-값 쌍)
     * @return 업데이트된 설정 목록
     */
    @PutMapping("/batch")
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> batchUpdateSettings(
            @RequestBody Map<String, String> settings) {
        try {
            log.info("설정 일괄 업데이트 요청 처리: {} 개 설정", settings.size());
            
            Long adminId = getCurrentUserId();
            
            if (settings.isEmpty()) {
                log.warn("설정 일괄 업데이트 실패: 빈 설정 맵");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "업데이트할 설정이 없습니다.", null));
            }
            
            // 서비스 계층에 일괄 업데이트 요청 위임
            List<SystemSettingDto> updatedSettings = systemSettingService.batchUpdateSettings(settings, adminId);
            
            log.info("설정 일괄 업데이트 성공: {} 개 설정", updatedSettings.size());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 일괄 업데이트 성공", updatedSettings));
        } catch (IllegalArgumentException e) {
            log.warn("설정 일괄 업데이트 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("설정 일괄 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
    
    /**
     * 시스템 설정 내보내기
     * 
     * @param category 선택적 카테고리 필터
     * @return 설정 목록(JSON 형식)
     */
    @GetMapping("/export")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exportSettings(
            @RequestParam(required = false) SettingCategory category) {
        try {
            log.info("설정 내보내기 요청 처리: 카테고리={}", category);
            
            // 서비스 계층에 내보내기 요청 위임
            Map<String, Object> exportData = systemSettingService.exportSettings(category);
            
            log.info("설정 내보내기 성공: {} 개 항목", exportData.size());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 내보내기 성공", exportData));
        } catch (Exception e) {
            log.error("설정 내보내기 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
    
    /**
     * 시스템 설정 가져오기
     * 
     * @param settings 가져올 설정 데이터
     * @param overwrite 기존 설정 덮어쓰기 여부
     * @return 가져온 설정 목록
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> importSettings(
            @RequestBody Map<String, Object> settings,
            @RequestParam(defaultValue = "false") boolean overwrite) {
        try {
            log.info("설정 가져오기 요청 처리: {} 개 설정, 덮어쓰기={}", settings.size(), overwrite);
            
            Long adminId = getCurrentUserId();
            
            if (settings.isEmpty()) {
                log.warn("설정 가져오기 실패: 빈 설정 데이터");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("ERROR", "가져올 설정 데이터가 없습니다.", null));
            }
            
            // 서비스 계층에 가져오기 요청 위임
            List<SystemSettingDto> importedSettings = systemSettingService.importSettings(settings, overwrite, adminId);
            
            log.info("설정 가져오기 성공: {} 개 설정", importedSettings.size());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "설정 가져오기 성공", importedSettings));
        } catch (IllegalArgumentException e) {
            log.warn("설정 가져오기 실패: 유효성 검사 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (ClassCastException e) {
            log.warn("설정 가져오기 실패: 데이터 형식 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", "데이터 형식이 올바르지 않습니다: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("설정 가져오기 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 