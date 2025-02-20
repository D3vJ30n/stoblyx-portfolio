package com.j30n.stoblyx.common.security;

import com.j30n.stoblyx.application.usecase.user.port.FindUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security에서 사용자 인증을 위한 커스텀 UserDetailsService 구현체
 * 사용자 ID를 기반으로 사용자 정보를 조회하고 Spring Security의 UserDetails 객체로 변환합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final FindUserUseCase findUserUseCase;

    /**
     * 사용자 ID를 기반으로 UserDetails 객체를 생성합니다.
     *
     * @param userId 조회할 사용자 ID (문자열)
     * @return Spring Security의 UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없거나 ID 형식이 잘못된 경우
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            // 사용자 ID를 Long 타입으로 변환
            Long userIdLong = Long.parseLong(userId);
            
            // 사용자 정보 조회
            FindUserUseCase.UserResponse userResponse = findUserUseCase.findById(userIdLong);
            if (userResponse == null) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
            }

            // Spring Security의 User 객체 생성
            return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(userResponse.id()))
                .password(userResponse.password())
                .authorities(Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + userResponse.role().name())
                ))
                .build();

        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: " + userId, e);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("사용자 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
}