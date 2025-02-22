package com.j30n.stoblyx.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityAlreadyExistsException extends DomainException {
    private final String entityName;
    private final Object entityId;

    public EntityAlreadyExistsException(String entityName, Object entityId) {
        super(
            String.format("%s가 이미 존재합니다 (ID: %s)", entityName, entityId),
            HttpStatus.CONFLICT
        );
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public EntityAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
        this.entityName = null;
        this.entityId = null;
    }

    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
        this.entityName = null;
        this.entityId = null;
    }
}