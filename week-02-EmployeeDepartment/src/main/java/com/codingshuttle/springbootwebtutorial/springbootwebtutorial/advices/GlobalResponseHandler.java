package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.advices;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//Why beforeBodyWrite() Is Executed Automatically
//Spring Web has a built-in lifecycle for processing controller responses.
//🔄 Response Lifecycle (simplified)
//1️⃣ Controller returns something →
//ResponseEntity<EmployeeDTO>
//2️⃣ Spring MVC prepares to serialize the body to JSON using an HttpMessageConverter
//3️⃣ Before writing the body, Spring checks all beans implementing ResponseBodyAdvice interface
//4️⃣ Your GlobalResponseHandler is discovered and automatically triggered
//📌 Why it finds your handler automatically?
//Because you used:
//@RestControllerAdvice
//This annotation ensures this class is:
//✔ Auto-detected
//✔ Applied across all controllers
//✔ Runs for REST responses only
//✔ Intercepts JSON serialization
//🌐 How Spring chooses to run it
//Spring calls:
//supports()
//This method tells Spring whether your advice should handle the response or not.
//You returned:
//return true;
//Meaning:
//Hey Spring, intercept every response, no exceptions!
//So Spring calls:
//beforeBodyWrite()
//to modify your result just before sending JSON to Postman.
//🧠 What beforeBodyWrite() is doing

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body instanceof ApiResponse<?>) {
            return body;
        }

        return new ApiResponse<>(body);
    }
}
