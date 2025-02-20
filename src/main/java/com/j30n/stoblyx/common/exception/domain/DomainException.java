package com.j30n.stoblyx.common.exception.domain;

/**
 * 도메인 계층에서 발생하는 모든 예외의 기본 클래스
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
} 