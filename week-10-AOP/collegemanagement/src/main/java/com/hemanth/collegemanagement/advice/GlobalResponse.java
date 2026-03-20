package com.hemanth.collegemanagement.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
