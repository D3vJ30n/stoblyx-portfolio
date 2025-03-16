package com.j30n.stoblyx.domain.model;

import com.j30n.stoblyx.domain.enums.SettingCategory;
import com.j30n.stoblyx.domain.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 시스템 설정 정보를 저장하는 엔티티 클래스
 * API 키, 리소스 경로, 캐시 설정, 랭킹 시스템 설정, 게이미피케이션 설정 등을 관리
 */
@Entity
@Table(name = "system_settings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 설정 키 (고유 식별자)
     */
    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String key;

    /**
     * 설정 값
     */
    @Column(name = "setting_value", nullable = false, length = 1000)
    private String value;

    /**
     * 설정 설명
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 설정 카테고리
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SettingCategory category;

    /**
     * 설정이 암호화되어야 하는지 여부 (API 키 등)
     */
    @Column(name = "is_encrypted", nullable = false)
    private boolean encrypted;

    /**
     * 설정이 시스템에 의해 자동으로 관리되는지 여부
     */
    @Column(name = "is_system_managed", nullable = false)
    private boolean systemManaged;

    /**
     * 마지막 수정자 ID (관리자)
     */
    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    /**
     * 설정의 기본값
     */
    @Column(name = "default_value", length = 1000)
    private String defaultValue;

    /**
     * 설정 값 유효성 검사를 위한 정규식 패턴
     */
    @Column(name = "validation_pattern", length = 255)
    private String validationPattern;
} 