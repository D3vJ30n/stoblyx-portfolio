package com.j30n.stoblyx.config;

import com.j30n.stoblyx.infrastructure.annotation.CurrentUser;
import com.j30n.stoblyx.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 테스트에서 사용할 ArgumentResolver 모음
 */
@Slf4j
public class ArgumentTestResolvers {

    /**
     * 테스트에서 @CurrentUser 어노테이션을 처리하기 위한 ArgumentResolver
     */
    @Slf4j
    public static class CurrentUserArgumentTestResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(@NonNull MethodParameter parameter) {
            boolean supports = parameter.getParameterAnnotation(CurrentUser.class) != null;
            System.out.println("CurrentUserArgumentTestResolver.supportsParameter 호출됨: " + parameter.getParameterName() + " = " + supports);
            return supports;
        }

        @Override
        public Object resolveArgument(@NonNull MethodParameter parameter,
                                      @Nullable ModelAndViewContainer mavContainer,
                                      @NonNull NativeWebRequest webRequest,
                                      @NonNull WebDataBinderFactory binderFactory) {
            System.out.println("CurrentUserArgumentTestResolver.resolveArgument 호출됨");
            log.debug("파라미터 정보: {}", parameter.getParameterName());
            log.debug("파라미터 타입: {}", parameter.getParameterType().getName());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                System.out.println("Authentication이 null입니다. 테스트 환경에서는 SecurityContext에 인증 정보가 제대로 설정되지 않았습니다.");
                log.debug("Authentication이 null입니다.");

                // 테스트용 인증 정보 생성
                System.out.println("테스트용 기본 UserPrincipal을 생성합니다.");
                return UserPrincipal.builder()
                    .id(1L)
                    .username("testuser")
                    .email("testuser@example.com")
                    .role("USER")
                    .build();
            }

            Object principal = authentication.getPrincipal();
            log.debug("Principal 타입: {}", principal.getClass().getName());
            log.debug("Principal 정보: {}", principal);
            log.debug("인증 정보: {}", authentication.getAuthorities());

            if (principal instanceof UserPrincipal) {
                log.debug("Principal이 UserPrincipal 타입입니다.");
                return principal;
            } else if (principal instanceof User user) {
                log.debug("Principal이 User 타입입니다. UserPrincipal로 변환합니다.");
                return UserPrincipal.builder()
                    .id(1L) // 테스트용 ID
                    .username(user.getUsername())
                    .email(user.getUsername() + "@example.com") // 테스트용 이메일
                    .password(user.getPassword())
                    .role("USER")
                    .authorities(user.getAuthorities())
                    .build();
            } else if (principal instanceof UserDetails userDetails) {
                log.debug("Principal이 UserDetails 타입입니다. UserPrincipal로 변환합니다.");
                return UserPrincipal.builder()
                    .id(1L) // 테스트용 ID
                    .username(userDetails.getUsername())
                    .email(userDetails.getUsername() + "@example.com") // 테스트용 이메일
                    .password(userDetails.getPassword())
                    .role("USER")
                    .authorities(userDetails.getAuthorities())
                    .build();
            } else if (principal instanceof String username) {
                log.debug("Principal이 String 타입입니다. UserPrincipal로 변환합니다.");
                return UserPrincipal.builder()
                    .id(1L) // 테스트용 ID
                    .username(username)
                    .email(username + "@example.com") // 테스트용 이메일
                    .role("USER")
                    .build();
            }

            // 마지막 방어 코드로, 어떤 타입이든 UserPrincipal로 변환 시도
            log.debug("지원하지 않는 Principal 타입입니다: {}. 기본 UserPrincipal을 생성합니다.", principal.getClass().getName());
            return UserPrincipal.builder()
                .id(1L) // 테스트용 ID
                .username("testuser") // 테스트용 기본 사용자명
                .email("testuser@example.com") // 테스트용 이메일
                .role("USER")
                .authorities(authentication.getAuthorities())
                .build();
        }
    }
} 