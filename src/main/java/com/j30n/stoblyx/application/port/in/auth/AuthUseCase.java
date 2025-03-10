package com.j30n.stoblyx.application.port.in.auth;

import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.SignUpRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.TokenResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.PasswordChangeRequest;

/**
 * 인증 관련 유스케이스 인터페이스
 */
public interface AuthUseCase {
    /**
     * 회원가입
     *
     * @param request 회원가입 요청 정보
     */
    void signUp(SignUpRequest request);

    /**
     * 로그인
     *
     * @param request 로그인 요청 정보
     * @return 토큰 응답
     */
    TokenResponse login(LoginRequest request);

    /**
     * 토큰 갱신
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 토큰 응답
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * 로그아웃
     *
     * @param accessToken 액세스 토큰
     */
    void logout(String accessToken);

    /**
     * 비밀번호 변경
     *
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청 정보
     */
    void changePassword(Long userId, PasswordChangeRequest request);
}
