package com.example.tmgjavatest.exception.handling;

import com.example.tmgjavatest.exception.EmptyStackException;
import com.example.tmgjavatest.exception.NoKeyValuePairException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessages);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessages);
    }

    @ExceptionHandler(EmptyStackException.class)
    public ResponseEntity<ErrorResponse> handleException(EmptyStackException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(NoKeyValuePairException.class)
    public ResponseEntity<ErrorResponse> handleException(NoKeyValuePairException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, Collections.singletonList(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, List<String> errorMessages) {
        return new ResponseEntity<>(new ErrorResponse(status, Instant.now(), errorMessages), new HttpHeaders(), status);
    }

    private record ErrorResponse(HttpStatus status, Instant timestamp, List<String> errors) { }
}
