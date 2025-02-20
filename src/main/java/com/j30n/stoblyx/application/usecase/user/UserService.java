package com.j30n.stoblyx.application.usecase.user;

import com.j30n.stoblyx.application.usecase.user.port.FindUserUseCase;
import com.j30n.stoblyx.application.usecase.user.port.LoginUserUseCase;
import com.j30n.stoblyx.application.usecase.user.port.RegisterUserUseCase;
import com.j30n.stoblyx.common.exception.user.InvalidPasswordException;
import com.j30n.stoblyx.common.exception.user.UserAlreadyExistsException;
import com.j30n.stoblyx.common.exception.user.UserNotFoundException;
import com.j30n.stoblyx.common.security.JwtProvider;
import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.domain.port.out.user.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements RegisterUserUseCase, LoginUserUseCase, FindUserUseCase {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public RegisterUserResponse register(RegisterUserCommand command) {
        if (userPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        User user = User.builder()
            .email(command.email())
            .password(passwordEncoder.encode(command.password()))
            .name(command.name())
            .role(User.Role.USER)
            .build();

        user = userPort.save(user);

        return new RegisterUserResponse(
            user.getId(),
            user.getEmail(),
            user.getName()
        );
    }

    @Override
    public LoginUserResponse login(LoginUserCommand command) {
        User user = userPort.findByEmail(command.email())
            .orElseThrow(() -> new UserNotFoundException(command.email()));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return new LoginUserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            accessToken,
            refreshToken
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userPort.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPassword(),
            user.getRole()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userPort.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));

        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPassword(),
            user.getRole()
        );
    }
} 