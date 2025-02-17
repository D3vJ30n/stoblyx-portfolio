package com.j30n.stoblyx.adapter.in.web;

import com.j30n.stoblyx.common.security.JwtProvider;
import com.j30n.stoblyx.domain.user.User;
import com.j30n.stoblyx.port.in.UserUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API를 처리하는 컨트롤러
 * 회원가입과 로그인 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserUseCase userUseCase;
    private final JwtProvider jwtProvider;

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param request 사용자 등록 정보 (이메일, 비밀번호, 이름)
     * @return 등록된 사용자 정보
     * @throws IllegalArgumentException 이메일이 이미 존재하는 경우
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        try {
            User user = userUseCase.registerUser(
                request.email(),
                request.password(),
                request.name()
            );
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("An error occurred during registration"));
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userUseCase.findUserByEmail(request.email());
            userUseCase.validateUser(request.email(), request.password());
            
            String token = jwtProvider.createToken(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Login successful", new LoginResponse(token)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("An error occurred during login"));
        }
    }

    // Request/Response Records with validation
    record RegisterUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name
    ) {}

    record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
    ) {}

    record LoginResponse(String token) {}
}

/**
 * API 응답을 위한 공통 포맷
 */
record ApiResponse<T>(String status, String message, T data) {
    static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null);
    }
} 