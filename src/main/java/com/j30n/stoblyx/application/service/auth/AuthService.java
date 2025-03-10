package com.j30n.stoblyx.application.service.auth;

import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.SignUpRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.TokenResponse;
import com.j30n.stoblyx.adapter.in.web.dto.user.PasswordChangeRequest;
import com.j30n.stoblyx.application.port.in.auth.AuthUseCase;
import com.j30n.stoblyx.application.port.out.auth.AuthPort;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.infrastructure.security.JwtTokenProvider;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthPort authPort;

    @Override
    @Transactional
    public void signUp(SignUpRequest request) {
        if (authPort.findUserByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .username(request.username())
            .nickname(request.nickname())
            .build();

        authPort.saveUser(user);
        log.info("회원가입 완료: {}", user.getEmail());
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(request.username());

        Long userId = Long.parseLong(authentication.getName());
        authPort.saveRefreshToken(userId, refreshToken, tokenProvider.getRefreshTokenValidityInMilliseconds() / 1000);

        log.info("로그인 완료: {}", request.username());
        return TokenResponse.of(accessToken, refreshToken, tokenProvider.getAccessTokenValidityInMilliseconds() / 1000);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String userId = authPort.findUserIdByRefreshToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리프레시 토큰입니다."));

        String username = tokenProvider.getUsername(refreshToken);
        User user = authPort.findUserByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            principal,
            null,
            AuthorityUtils.NO_AUTHORITIES
        );

        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(username);

        authPort.deleteRefreshToken(refreshToken);
        authPort.saveRefreshToken(Long.parseLong(userId), newRefreshToken, tokenProvider.getRefreshTokenValidityInMilliseconds() / 1000);

        log.info("토큰 갱신 완료: userId={}", userId);
        return TokenResponse.of(newAccessToken, newRefreshToken, tokenProvider.getAccessTokenValidityInMilliseconds() / 1000);
    }

    @Override
    public void logout(String accessToken) {
        if (!tokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 액세스 토큰입니다.");
        }

        String username = tokenProvider.getUsername(accessToken);
        long remainingValidityInSeconds = tokenProvider.getRemainingValidityInSeconds(accessToken);

        authPort.addToBlacklist(accessToken, remainingValidityInSeconds);
        log.info("로그아웃 완료: username={}", username);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        // 사용자 조회
        User user = authPort.findUserById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 새 비밀번호와 확인 비밀번호 일치 여부 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        
        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        authPort.saveUser(user);
        
        log.info("비밀번호 변경 완료: userId={}", userId);
    }
}