package com.management.inventory.exception;

import com.management.inventory.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse> exceptionHandler(Exception e) {
        final ApiResponse response = ApiResponse.error(e.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse> exceptionHandler(HttpMessageNotReadableException e) {
        final ApiResponse response = ApiResponse.error(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiResponse> apiExceptionHandler(ApiException e) {
        final ApiResponse response = ApiResponse.fail(e.getBaseResponse());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
