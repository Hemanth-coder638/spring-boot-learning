package com.hemanth.collegemanagement.advice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    // handle common not-found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GlobalResponse<Object>> handleNotFound(EntityNotFoundException ex){
        GlobalResponse<Object> r = new GlobalResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(r, HttpStatus.NOT_FOUND);
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<Object>> handleAll(Exception ex){
        GlobalResponse<Object> r = new GlobalResponse<>(false, "Server error: " + ex.getMessage(), null);
        return new ResponseEntity<>(r, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
