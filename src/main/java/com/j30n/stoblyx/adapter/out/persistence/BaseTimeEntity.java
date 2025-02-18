package com.j30n.stoblyx.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {
    
    @CreatedDate
    @Column(updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Comment("수정일시")
    private LocalDateTime updatedAt;
    
    @Column(length = 100)
    @Comment("생성자")
    private String createdBy;
    
    @Column(length = 100)
    @Comment("수정자")
    private String modifiedBy;
}