package com.example.sbg.exceptions;

import com.example.sbg.api.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Error errorDetails = new Error(HttpStatus.NOT_FOUND.value(), 103, ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> handleBadRequestException(BadRequestException ex, WebRequest request) {
        Error errorDetails = new Error(HttpStatus.BAD_REQUEST.value(), 101, ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGlobalException(Exception ex, WebRequest request) {
        Error errorDetails = new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), 103, ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
