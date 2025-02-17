package com.j30n.stoblyx.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT 인증 필터 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();

        userDetails = new User(
            "testUser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공")
    void whenValidToken_thenAuthenticates() throws Exception {
        // given
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        given(jwtProvider.validateToken(token)).willReturn(true);
        given(jwtProvider.getUserIdentifierFromToken(token)).willReturn("testUser");
        given(userDetailsService.loadUserByUsername("testUser")).willReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .isEqualTo(userDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없는 경우")
    void whenNoAuthorizationHeader_thenContinuesFilterChain() throws Exception {
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("잘못된 형식의 Authorization 헤더")
    void whenInvalidAuthorizationHeader_thenContinuesFilterChain() throws Exception {
        // given
        request.addHeader("Authorization", "InvalidFormat token");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtProvider, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰")
    void whenInvalidToken_thenClearsSecurityContextAndContinuesFilterChain() throws Exception {
        // given
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        given(jwtProvider.validateToken(token)).willReturn(false);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    @DisplayName("토큰 검증 중 예외 발생")
    void whenExceptionOccurs_thenClearsSecurityContextAndContinuesFilterChain() throws Exception {
        // given
        String token = "exception.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        given(jwtProvider.validateToken(token)).willThrow(new RuntimeException("Token validation failed"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
} 