package com.j30n.stoblyx.common.security;

import com.j30n.stoblyx.domain.model.user.User;
import com.j30n.stoblyx.port.in.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserUseCase userUseCase;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            if (!userId.matches("\\d+")) {
                throw new UsernameNotFoundException("Invalid user ID format");
            }

            Long userIdLong = Long.parseLong(userId);
            User user = userUseCase.findUserById(userIdLong);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with id: " + userId);
            }

            return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format", e);
        }
    }
}
