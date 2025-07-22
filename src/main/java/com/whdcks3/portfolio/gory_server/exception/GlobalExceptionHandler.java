package com.whdcks3.portfolio.gory_server.exception;

import java.net.BindException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationArgumentException(
            ConstraintViolationException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindArgumentException(BindException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(MemberNotEqualsException.class)
    public ResponseEntity<Map<String, Object>> handleMemberNotEqualException(MemberNotEqualsException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), e);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(UnSupportedImageFormatException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedImageFormatException(
            UnSupportedImageFormatException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<Map<String, Object>> handleJwtTokenException(JwtTokenException e) {
        System.out.println("JwtTokenException: " + e.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
    }

    ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, Exception e) {
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        StackTraceElement errorLocation = (e != null && e.getStackTrace().length > 0) ? e.getStackTrace()[0] : null;

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);

        if (errorLocation != null) {
            errorResponse.put("class", errorLocation.getClassName());
            errorResponse.put("method", errorLocation.getMethodName());
            errorResponse.put("line", errorLocation.getLineNumber());
        }

        return new ResponseEntity<>(errorResponse, status);
    }
}
