package com.j30n.stoblyx.application.service.auth;

import com.j30n.stoblyx.adapter.in.web.dto.auth.LoginRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.SignUpRequest;
import com.j30n.stoblyx.adapter.in.web.dto.auth.TokenResponse;

public interface AuthService {
    void signUp(SignUpRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String accessToken);
} 