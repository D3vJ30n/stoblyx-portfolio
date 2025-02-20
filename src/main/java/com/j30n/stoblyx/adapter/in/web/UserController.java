package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.application.usecase.user.port.FindUserUseCase;
import com.j30n.stoblyx.application.usecase.user.port.LoginUserUseCase;
import com.j30n.stoblyx.application.usecase.user.port.RegisterUserUseCase;
import com.j30n.stoblyx.common.dto.ApiResponse;
import com.j30n.stoblyx.common.security.JwtProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * 회원가입과 로그인 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final JwtProvider jwtProvider;

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param request 사용자 등록 정보 (이메일, 비밀번호, 이름)
     * @return 등록된 사용자 정보
     * @throws IllegalArgumentException 이메일이 이미 존재하는 경우
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterUserUseCase.RegisterUserResponse>> registerUser(
        @Valid @RequestBody RegisterUserRequest request) {
        try {
            var command = new RegisterUserUseCase.RegisterUserCommand(
                request.email(),
                request.password(),
                request.name()
            );

            var response = registerUserUseCase.register(command);
            return ResponseEntity.ok(ApiResponse.success("사용자 등록이 완료되었습니다.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("사용자 등록 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인 정보 (이메일, 비밀번호)
     * @return JWT 토큰
     * @throws IllegalArgumentException 이메일이나 비밀번호가 일치하지 않는 경우
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginUserUseCase.LoginUserResponse>> login(
        @Valid @RequestBody LoginRequest request) {
        try {
            var command = new LoginUserUseCase.LoginUserCommand(
                request.email(),
                request.password()
            );

            var response = loginUserUseCase.login(command);
            return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("로그인 중 오류가 발생했습니다."));
        }
    }

    // Request/Response Records with validation
    record RegisterUserRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
        String password,

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
        String name
    ) {
    }

    record LoginRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
    ) {
    }
} 