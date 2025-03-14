package com.j30n.stoblyx.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 컴포넌트
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            UserDetailsService userDetailsService
        ) {
            this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
            this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
            this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
            this.userDetailsService = userDetailsService;
            log.info("JWT 토큰 프로바이더 초기화 완료: 액세스 토큰 유효 시간={}초, 리프레시 토큰 유효 시간={}초", 
                    accessTokenValidityInSeconds, refreshTokenValidityInSeconds);
        }

    public String createAccessToken(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            log.debug("액세스 토큰 생성: 사용자={}, ID={}", userPrincipal.getUsername(), userPrincipal.getId());
            return createToken(userPrincipal.getUsername(), userPrincipal.getId(), accessTokenValidityInMilliseconds);
        } catch (ClassCastException e) {
            log.error("토큰 생성 실패: 인증 객체의 Principal이 UserPrincipal 타입이 아닙니다: {}", e.getMessage());
            throw new IllegalArgumentException("인증 객체의 타입이 유효하지 않습니다", e);
        } catch (Exception e) {
            log.error("액세스 토큰 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new IllegalStateException("토큰 생성 오류", e);
        }
    }

    public String createRefreshToken(String username) {
        try {
            log.debug("리프레시 토큰 생성: 사용자={}", username);
            return createToken(username, null, refreshTokenValidityInMilliseconds);
        } catch (Exception e) {
            log.error("리프레시 토큰 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new IllegalStateException("토큰 생성 오류", e);
        }
    }

    private String createToken(String subject, Long userId, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        
        try {
            JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key);

            if (userId != null) {
                builder.claim("userId", userId);
            }

            String token = builder.compact();
            log.trace("토큰 생성 완료: 유효 기간={}, 타입={}", validity, userId != null ? "액세스 토큰" : "리프레시 토큰");
            return token;
        } catch (Exception e) {
            log.error("JWT 토큰 생성 실패: {}", e.getMessage(), e);
            throw new IllegalStateException("JWT 토큰 생성 실패", e);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            Claims claims = extractClaims(token);
            String username = claims.getSubject();
            log.debug("토큰에서 사용자 인증 정보 조회: 사용자={}", username);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            log.error("인증 처리 실패: 사용자를 찾을 수 없음: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("인증 처리 실패: 유효하지 않은 JWT 토큰: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰", e);
        } catch (Exception e) {
            log.error("인증 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new IllegalStateException("인증 처리 오류", e);
        }
    }

    public boolean validateToken(String token) {
        if (token == null) {
            log.warn("토큰 검증 실패: 토큰이 null입니다");
            return false;
        }
        
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("유효하지 않은 JWT 서명: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 JWT 클레임: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("토큰 검증 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            String username = extractClaims(token).getSubject();
            log.debug("토큰에서 사용자 이름 추출: {}", username);
            return username;
        } catch (JwtException e) {
            log.error("토큰에서 사용자 이름 추출 실패: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("토큰에서 사용자 이름 추출 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.debug("JWT 토큰 클레임 추출 실패: {}", e.getMessage());
            throw e;
        }
    }

    public long getAccessTokenValidityInMilliseconds() {
        return accessTokenValidityInMilliseconds;
    }

    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }

    public long getRemainingValidityInSeconds(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            long remainingSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            log.debug("토큰 남은 유효 시간: {}초", remainingSeconds);
            return remainingSeconds;
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰의 남은 유효 시간을 확인하려고 했습니다: {}", e.getMessage());
            return 0;
        } catch (Exception e) {
            log.error("토큰 남은 유효 시간 확인 중 오류 발생: {}", e.getMessage(), e);
            return 0;
        }
    }
}