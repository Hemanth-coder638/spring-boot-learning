package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.advices;

import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
//🛡️ This annotation makes this class a Global Exception Handler
//It listens to all Controllers and catches exceptions thrown anywhere in API
public class GlobalExceptionHandler {

    //🔥 Handles only ResourceNotFoundException thrown from service/controller
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception) {

        //📌 Creates structured error response with clear error code
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND) // 404 Not Found
                .message(exception.getMessage())// message passed from thrown exception
                .build();

        //🚀 Wrap apiError inside ApiResponse and return JSON with correct status code
        return buildErrorResponseEntity(apiError);
    }

    //🔥 Handles any other unhandled exceptions — fallback/default handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception) {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Server Error
                .message(exception.getMessage()) // actual Java exception message
                .build();

        return buildErrorResponseEntity(apiError);
    }

    //🔥 Handles Input Validation failures triggered by @Valid annotations
    //Example: @NotNull, @Email, @Size, Custom Annotation Validation etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationErrors(MethodArgumentNotValidException exception) {

        //📌 Extracts all validation error messages (multiple field errors possible)
        List<String> errors = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage()) // converts ValidationError -> String msg
                .collect(Collectors.toList());

        //📌 Build ApiError including multiple detailed messages
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                .message("Input validation failed") // common headline message
                .subErrors(errors) // stores list of detailed error messages calling setter method
                .build();

        return buildErrorResponseEntity(apiError);
    }

    //💡 Common reusable method to convert ApiError into final ResponseEntity<ApiResponse>
    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {

        //✔ ApiResponse wraps the ApiError to maintain standard format for all API responses
        //✔ ResponseEntity sets proper HTTP status for the client
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

}
