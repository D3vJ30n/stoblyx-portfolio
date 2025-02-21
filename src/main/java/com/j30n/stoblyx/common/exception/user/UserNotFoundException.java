package com.j30n.stoblyx.common.exception.user;

import com.j30n.stoblyx.common.exception.domain.DomainException;

/**
 * 요청한 사용자를 찾을 수 없을 때 발생하는 예외
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String email) {
        super(String.format("사용자를 찾을 수 없습니다: %s", email));
    }

    public UserNotFoundException(Long id) {
        super(String.format("사용자를 찾을 수 없습니다. ID: %d", id));
    }
} 