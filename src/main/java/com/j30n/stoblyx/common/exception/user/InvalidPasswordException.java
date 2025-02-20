package com.j30n.stoblyx.common.exception.user;

import com.j30n.stoblyx.common.exception.domain.DomainException;

/**
 * 잘못된 비밀번호가 입력되었을 때 발생하는 예외
 */
public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
} 