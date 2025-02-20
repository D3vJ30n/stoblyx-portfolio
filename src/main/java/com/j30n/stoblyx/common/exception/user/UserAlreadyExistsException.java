package com.j30n.stoblyx.common.exception.user;

import com.j30n.stoblyx.common.exception.domain.DomainException;

/**
 * 이미 존재하는 사용자를 등록하려 할 때 발생하는 예외
 */
public class UserAlreadyExistsException extends DomainException {

    public UserAlreadyExistsException(String email) {
        super(String.format("이미 등록된 이메일입니다: %s", email));
    }
} 