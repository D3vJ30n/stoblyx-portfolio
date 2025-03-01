package com.j30n.stoblyx.domain.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 기본이 되는 클래스로, 생성 시간, 수정 시간, 삭제 상태를 관리합니다.
 * 이 클래스를 상속받은 모든 엔티티는 자동으로 이 필드들을 가지게 됩니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 엔티티를 논리적으로 삭제합니다.
     * 실제 데이터베이스에서 삭제하지 않고 삭제 상태만 변경합니다.
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 삭제된 엔티티를 복구합니다.
     */
    public void restore() {
        this.isDeleted = false;
    }

    /**
     * 엔티티의 삭제 상태를 확인합니다.
     * @return 엔티티가 삭제되었으면 true, 아니면 false를 반환
     */
    public boolean isDeleted() {
        return this.isDeleted;
    }

    /**
     * 수정 시간을 강제로 업데이트합니다.
     * 일반적으로는 Spring Data JPA에 의해 자동으로 처리되므로, 특별한 경우에만 사용합니다.
     */
    protected void updateModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }
} 