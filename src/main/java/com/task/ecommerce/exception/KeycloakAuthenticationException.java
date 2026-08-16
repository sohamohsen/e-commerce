package com.task.ecommerce.exception;

public class KeycloakAuthenticationException extends RuntimeException {
    public KeycloakAuthenticationException(String message) {
        super(message);
    }
}
