package com.j30n.stoblyx.infrastructure.security;

import com.j30n.stoblyx.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String email;
    private String password;
    private String role;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    
    // User 엔티티에서 UserPrincipal을 생성하는 정적 팩토리 메서드
    public static UserPrincipal create(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
    }
    
    public static UserPrincipal create(User user, List<SimpleGrantedAuthority> authorities) {
        UserPrincipal principal = create(user);
        // authorities를 실제로 적용하는 코드 작성
        principal.setAuthorities(authorities);  // 필요하다면 해당 메서드 구현
        return principal;
    }
    
    public void setAuthorities(List<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEmail() {
        return email;
    }
} 