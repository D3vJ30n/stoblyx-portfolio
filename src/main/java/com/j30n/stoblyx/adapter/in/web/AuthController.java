package com.j30n.stoblyx.adapter.in.web;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API를 처리하는 컨트롤러
 * 회원가입과 로그인 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final JwtProvider jwtProvider;

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param request 사용자 등록 정보 (이메일, 비밀번호, 이름)
     * @return 등록된 사용자 정보
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
        @Valid @RequestBody RegisterRequest request) {
        try {
            var command = new RegisterUserUseCase.RegisterUserCommand(
                request.email(),
                request.password(),
                request.name()
            );

            var response = registerUserUseCase.register(command);

            return ResponseEntity.ok(ApiResponse.success(
                "회원가입이 완료되었습니다.",
                new RegisterResponse(
                    response.id(),
                    response.email(),
                    response.name()
                )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("회원가입 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인 정보 (이메일, 비밀번호)
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
        @Valid @RequestBody LoginRequest request) {
        try {
            var command = new LoginUserUseCase.LoginUserCommand(
                request.email(),
                request.password()
            );

            var response = loginUserUseCase.login(command);

            return ResponseEntity.ok(ApiResponse.success(
                "로그인이 완료되었습니다.",
                new TokenResponse(
                    response.accessToken(),
                    response.refreshToken()
                )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("로그인 처리 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
        @Valid @RequestBody RefreshTokenRequest request) {
        try {
            if (!jwtProvider.validateToken(request.refreshToken()) ||
                !jwtProvider.isRefreshToken(request.refreshToken())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("유효하지 않은 리프레시 토큰입니다."));
            }

            String userId = jwtProvider.getUserIdentifierFromToken(request.refreshToken());
            String newAccessToken = jwtProvider.createAccessToken(Long.parseLong(userId));
            String newRefreshToken = jwtProvider.createRefreshToken(Long.parseLong(userId));

            return ResponseEntity.ok(ApiResponse.success(
                "토큰이 갱신되었습니다.",
                new TokenResponse(newAccessToken, newRefreshToken)
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("토큰 갱신 중 오류가 발생했습니다."));
        }
    }

    // Request/Response Records
    record RegisterRequest(
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

    record RegisterResponse(
        Long id,
        String email,
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

    record TokenResponse(
        String accessToken,
        String refreshToken
    ) {
    }

    record RefreshTokenRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다")
        String refreshToken
    ) {
    }
} 