package com.hemanth.librarymanagment.advice;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@JsonPropertyOrder({"localDateTime","data","apiError"})
public class ApiResponse<T> {
    private LocalDateTime localDateTime;
    private T data;
    private ApiError apiError;
    public ApiResponse(){
        localDateTime=LocalDateTime.now();
    }
    public ApiResponse(T data){
        this();
       this.data=data;
    }
    public ApiResponse(ApiError apiError){
        this();
        this.apiError=apiError;
    }
}
