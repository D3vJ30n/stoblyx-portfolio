package com.j30n.stoblyx.application.usecase.user.port;

/**
 * 사용자 등록을 위한 유스케이스
 */
public interface RegisterUserUseCase {
    RegisterUserResponse register(RegisterUserCommand command);

    record RegisterUserCommand(
        String email,
        String password,
        String name
    ) {
        public RegisterUserCommand {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("이메일은 필수입니다.");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("비밀번호는 필수입니다.");
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }
        }
    }

    record RegisterUserResponse(
        Long id,
        String email,
        String name
    ) {
    }
} 