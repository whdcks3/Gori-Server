package com.whdcks3.portfolio.gory_server.exception;

public class ValidationException extends RuntimeException {
    private int statusCode;

    public ValidationException(String message) {
        this(0, message);
    }

    public ValidationException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
