package com.j30n.stoblyx.application.usecase.user.port;

import com.j30n.stoblyx.domain.model.user.User;

/**
 * 사용자 관련 유스케이스를 정의하는 인터페이스
 */
public interface UserUseCase {
    User registerUser(String email, String password, String name);

    User findUserById(Long id);

    User findUserByEmail(String email);

    void validateUser(String email, String password);
    // 필요한 메서드들은 각각의 구체적인 유스케이스 인터페이스에서 정의됩니다.
    // - RegisterUserUseCase
    // - LoginUserUseCase
    // - FindUserUseCase
}