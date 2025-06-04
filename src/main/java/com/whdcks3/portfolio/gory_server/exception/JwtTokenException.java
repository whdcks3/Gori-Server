package com.whdcks3.portfolio.gory_server.exception;

public class JwtTokenException extends RuntimeException {
    private String message;

    public JwtTokenException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
