package com.j30n.stoblyx.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedException extends DomainException {
    private final String entityName;
    private final String operation;

    public UnauthorizedException(String entityName, String operation) {
        super(
            String.format("%s에 대한 %s 권한이 없습니다", entityName, operation),
            HttpStatus.FORBIDDEN
        );
        this.entityName = entityName;
        this.operation = operation;
    }

    public UnauthorizedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
        this.entityName = null;
        this.operation = null;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.FORBIDDEN, cause);
        this.entityName = null;
        this.operation = null;
    }
}