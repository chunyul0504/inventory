package com.management.inventory.exception;

import com.management.inventory.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    /* 프로젝트 공통 성공 외 실패 처리는 Exception 으로 진행.*/
//    @ExceptionHandler(CoreException.class)
//    public ApiResponse apiException(CoreException e) {
//        log.error("[ERROR HANDLER][API FAIL]", e);
//        return ApiResponse.fail(e.getBaseResponse());
//    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse> handlerException(Exception e) {
        final ApiResponse response = ApiResponse.error(e.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiResponse> handlerCustomApiException(ApiException e) {
        final ApiResponse response = ApiResponse.fail(e.getBaseResponse());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
