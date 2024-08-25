package com.hsbc.exception.auth;


//This exception is thrown when user authentication fails.
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}

