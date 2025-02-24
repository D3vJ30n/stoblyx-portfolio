package com.j30n.stoblyx.adapter.in.web.exception;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message) {
        super(message);
    }
}