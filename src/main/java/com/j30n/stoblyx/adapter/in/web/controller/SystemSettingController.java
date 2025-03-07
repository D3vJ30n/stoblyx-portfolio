package com.j30n.stoblyx.adapter.in.web.controller;

import com.j30n.stoblyx.adapter.in.web.dto.system.SystemSettingDto;
import com.j30n.stoblyx.application.service.system.SystemSettingService;
import com.j30n.stoblyx.common.response.ApiResponse;
import com.j30n.stoblyx.domain.enums.SettingCategory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 시스템 설정 관리를 위한 REST 컨트롤러
 */
@RestController
@RequestMapping("/admin/settings")
public class SystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    /**
     * 모든 시스템 설정을 조회합니다.
     *
     * @return 시스템 설정 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> getAllSettings() {
        try {
            List<SystemSettingDto> settings = systemSettingService.getAllSettings();
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 조회 성공", settings), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 카테고리별 시스템 설정을 조회합니다.
     *
     * @param category 설정 카테고리
     * @return 해당 카테고리의 시스템 설정 목록
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> getSettingsByCategory(@PathVariable SettingCategory category) {
        try {
            List<SystemSettingDto> settings = systemSettingService.getSettingsByCategory(category);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "카테고리별 시스템 설정 조회 성공", settings), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 설정 키로 시스템 설정을 조회합니다.
     *
     * @param key 설정 키
     * @return 시스템 설정
     */
    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<SystemSettingDto>> getSettingByKey(@PathVariable String key) {
        try {
            return systemSettingService.getSettingByKey(key)
                .map(setting -> new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 조회 성공", setting), HttpStatus.OK))
                .orElse(new ResponseEntity<>(new ApiResponse<>("ERROR", "존재하지 않는 설정 키입니다: " + key, null), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 새로운 시스템 설정을 생성합니다.
     *
     * @param settingDto 시스템 설정 DTO
     * @return 생성된 시스템 설정
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SystemSettingDto>> createSetting(@Valid @RequestBody SystemSettingDto settingDto) {
        try {
            // 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminId = 1L; // 임시 관리자 ID
            SystemSettingDto createdSetting = systemSettingService.createSetting(settingDto, adminId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 생성 성공", createdSetting), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 기존 시스템 설정을 업데이트합니다.
     *
     * @param key        설정 키
     * @param settingDto 업데이트할 시스템 설정 DTO
     * @return 업데이트된 시스템 설정
     */
    @PutMapping("/{key}")
    public ResponseEntity<ApiResponse<SystemSettingDto>> updateSetting(@PathVariable String key, @Valid @RequestBody SystemSettingDto settingDto) {
        try {
            // 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminId = 1L; // 임시 관리자 ID
            SystemSettingDto updatedSetting = systemSettingService.updateSetting(key, settingDto, adminId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 업데이트 성공", updatedSetting), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시스템 설정을 삭제합니다.
     *
     * @param key 설정 키
     * @return 삭제 결과
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse<Boolean>> deleteSetting(@PathVariable String key) {
        try {
            boolean result = systemSettingService.deleteSetting(key);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 삭제 성공", result), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 키 패턴으로 시스템 설정을 검색합니다.
     *
     * @param keyPattern 키 패턴
     * @return 검색된 시스템 설정 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SystemSettingDto>>> searchSettingsByKeyPattern(@RequestParam String keyPattern) {
        try {
            List<SystemSettingDto> settings = systemSettingService.searchSettingsByKeyPattern(keyPattern);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 검색 성공", settings), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 시스템 설정을 기본값으로 초기화합니다.
     *
     * @param key 설정 키
     * @return 초기화된 시스템 설정
     */
    @PostMapping("/{key}/reset")
    public ResponseEntity<ApiResponse<SystemSettingDto>> resetSettingToDefault(@PathVariable String key) {
        try {
            // 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminId = 1L; // 임시 관리자 ID
            SystemSettingDto resetSetting = systemSettingService.resetSettingToDefault(key, adminId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "시스템 설정 초기화 성공", resetSetting), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Redis 캐시 TTL을 설정합니다.
     *
     * @param cacheName  캐시 이름
     * @param ttlSeconds TTL (초)
     * @return 업데이트된 시스템 설정
     */
    @PostMapping("/cache/{cacheName}/ttl")
    public ResponseEntity<ApiResponse<SystemSettingDto>> setCacheTTL(@PathVariable String cacheName, @RequestParam int ttlSeconds) {
        try {
            // 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminId = 1L; // 임시 관리자 ID
            SystemSettingDto updatedSetting = systemSettingService.setCacheTTL(cacheName, ttlSeconds, adminId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "캐시 TTL 설정 성공", updatedSetting), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 랭킹 시스템 알고리즘 파라미터를 설정합니다.
     *
     * @param paramName  파라미터 이름
     * @param paramValue 파라미터 값
     * @return 업데이트된 시스템 설정
     */
    @PostMapping("/ranking/param")
    public ResponseEntity<ApiResponse<SystemSettingDto>> setRankingParameter(@RequestParam String paramName, @RequestParam String paramValue) {
        try {
            // 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminId = 1L; // 임시 관리자 ID
            SystemSettingDto updatedSetting = systemSettingService.setRankingParameter(paramName, paramValue, adminId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "랭킹 파라미터 설정 성공", updatedSetting), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게이미피케이션 랭크별 혜택을 설정합니다.
     *
     * @param rankName    랭크 이름
     * @param benefitJson 혜택 JSON 문자열
     * @return 업데이트된 시스템 설정
     */
    @PostMapping("/gamification/rank/benefit")
    public ResponseEntity<ApiResponse<SystemSettingDto>> setRankBenefit(@RequestParam String rankName, @RequestBody String benefitJson) {
        try {
            // 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminId = 1L; // 임시 관리자 ID
            SystemSettingDto updatedSetting = systemSettingService.setRankBenefit(rankName, benefitJson, adminId);
            return new ResponseEntity<>(new ApiResponse<>("SUCCESS", "랭크 혜택 설정 성공", updatedSetting), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("ERROR", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 