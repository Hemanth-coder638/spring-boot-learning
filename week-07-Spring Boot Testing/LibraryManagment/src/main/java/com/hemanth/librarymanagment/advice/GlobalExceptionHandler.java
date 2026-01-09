package com.hemanth.librarymanagment.advice;

import com.hemanth.librarymanagment.exception.ResourceAlreadyExistException;
import com.hemanth.librarymanagment.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

//@RestControllerAdvice
public class GlobalExceptionHandler {
     @ExceptionHandler(ResourceNotFoundException.class)
     public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception){
         ApiError apiError=ApiError.builder().status(HttpStatus.NOT_FOUND).message(exception.getMessage()).build();
         return new ResponseEntity<>(new ApiResponse<>(apiError),HttpStatus.NOT_FOUND);
     }

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<ApiResponse<?>> handleMethodArgumentException(MethodArgumentNotValidException exception){
         List<String> errorsList=exception.getAllErrors().stream().map(e->e.getDefaultMessage()).toList();
         ApiError apiError=ApiError.builder().status(HttpStatus.BAD_REQUEST).message("Input Validation exceptions").stringList(errorsList).build();
         return buildResponseEntity(apiError);
     }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception){
         ApiError apiError=ApiError.builder().message(exception.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         return buildResponseEntity(apiError);
     }

     @ExceptionHandler(ResourceAlreadyExistException.class)
     public ResponseEntity<ApiResponse<?>> handleDuplicateValues(Exception exception){
         ApiError apiError=ApiError.builder().status(HttpStatus.BAD_REQUEST).message(exception.getMessage()).build();
         return buildResponseEntity(apiError);
     }
     public ResponseEntity<ApiResponse<?>> buildResponseEntity(ApiError apiError){
         return new ResponseEntity<>(new ApiResponse(apiError),apiError.getStatus());
     }
}
