package com.j30n.stoblyx.application.service.auth;

import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.SignUpRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.TokenResponse;
import com.j30n.stoblyx.domain.model.User;
import com.j30n.stoblyx.domain.repository.UserRepository;
import com.j30n.stoblyx.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public void signUp(SignUpRequest request) {
        log.debug("회원가입 시도 - username: {}, email: {}", request.username(), request.email());
        
        if (userRepository.existsByUsername(request.username())) {
            log.debug("회원가입 실패 - 이미 존재하는 username: {}", request.username());
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.debug("회원가입 실패 - 이미 존재하는 email: {}", request.email());
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        try {
            User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .email(request.email())
                .build();

            userRepository.save(user);
            log.debug("회원가입 성공 - username: {}", request.username());
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(request.username());

        redisTemplate.opsForValue().set(
            "RT:" + request.username(),
            refreshToken,
            tokenProvider.getRefreshTokenValidityInMilliseconds(),
            TimeUnit.MILLISECONDS
        );

        return TokenResponse.of(accessToken, refreshToken, tokenProvider.getAccessTokenValidityInMilliseconds());
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String username = tokenProvider.getUsername(refreshToken);
        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + username);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("저장된 토큰과 일치하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(username);

        redisTemplate.opsForValue().set(
            "RT:" + username,
            newRefreshToken,
            tokenProvider.getRefreshTokenValidityInMilliseconds(),
            TimeUnit.MILLISECONDS
        );

        return TokenResponse.of(newAccessToken, newRefreshToken, tokenProvider.getAccessTokenValidityInMilliseconds());
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        if (!tokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String username = authentication.getName();

        redisTemplate.delete("RT:" + username);
        redisTemplate.opsForValue().set(
            "BL:" + accessToken,
            "logout",
            tokenProvider.getAccessTokenValidityInMilliseconds(),
            TimeUnit.MILLISECONDS
        );
    }
} 