package com.hsbc.exception.auth;


// This exception is thrown when a user tries to perform an action they are not authorized to do.
public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}
