package com.j30n.stoblyx.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityNotFoundException extends DomainException {
    private final String entityName;
    private final Object entityId;

    public EntityNotFoundException(String entityName, Object entityId) {
        super(
            String.format("%s를 찾을 수 없습니다 (ID: %s)", entityName, entityId),
            HttpStatus.NOT_FOUND
        );
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
        this.entityName = null;
        this.entityId = null;
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, cause);
        this.entityName = null;
        this.entityId = null;
    }
}