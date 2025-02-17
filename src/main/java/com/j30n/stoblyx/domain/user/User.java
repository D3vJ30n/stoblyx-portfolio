package com.j30n.stoblyx.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {
    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final Role role;

    @Builder
    public User(Long id, String email, String password, String name, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public enum Role {
        USER, ADMIN
    }
} 