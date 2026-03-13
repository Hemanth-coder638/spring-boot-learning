package com.hemanth.librarymanagment.advice;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ApiError {
    private String message;
    private List<String> stringList;
    private HttpStatus status;
}
