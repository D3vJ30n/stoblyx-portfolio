package com.j30n.stoblyx.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 기본이 되는 시간 관련 기본 엔티티
 * Spring Data JPA의 Auditing 기능을 사용하여 생성/수정 시간과 생성자/수정자 정보를 자동으로 관리합니다.
 *
 * @CreatedDate: 엔티티가 생성될 때 시간이 자동으로 저장
 * @LastModifiedDate: 엔티티가 수정될 때 시간이 자동으로 저장
 * @CreatedBy: 엔티티를 생성한 사용자의 정보가 자동으로 저장
 * @LastModifiedBy: 엔티티를 수정한 사용자의 정보가 자동으로 저장
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(length = 100)
    private String modifiedBy;
}