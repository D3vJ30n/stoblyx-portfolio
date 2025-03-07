package com.j30n.stoblyx.adapter.in.web.dto.system;

import com.j30n.stoblyx.domain.enums.SettingCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 시스템 설정 데이터 전송 객체
 */
public record SystemSettingDto(
    Long id,

    @NotBlank(message = "설정 키는 필수 입력값입니다.")
    @Size(max = 100, message = "설정 키는 최대 100자까지 입력 가능합니다.")
    String key,

    @NotBlank(message = "설정 값은 필수 입력값입니다.")
    @Size(max = 1000, message = "설정 값은 최대 1000자까지 입력 가능합니다.")
    String value,

    @Size(max = 500, message = "설정 설명은 최대 500자까지 입력 가능합니다.")
    String description,

    @NotNull(message = "설정 카테고리는 필수 입력값입니다.")
    SettingCategory category,

    boolean encrypted,

    boolean systemManaged,

    Long lastModifiedBy,

    @Size(max = 1000, message = "기본값은 최대 1000자까지 입력 가능합니다.")
    String defaultValue,

    @Size(max = 255, message = "유효성 검사 패턴은 최대 255자까지 입력 가능합니다.")
    String validationPattern
) {
    /**
     * 유효성 검사를 포함한 생성자
     */
    public SystemSettingDto {
        if (key != null && key.isBlank()) {
            throw new IllegalArgumentException("설정 키는 공백일 수 없습니다.");
        }

        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("설정 값은 공백일 수 없습니다.");
        }

        if (category == null) {
            throw new IllegalArgumentException("설정 카테고리는 null일 수 없습니다.");
        }
    }
} 