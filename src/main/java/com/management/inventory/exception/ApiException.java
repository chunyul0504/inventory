package com.management.inventory.exception;


import com.management.inventory.response.BaseResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;

@Slf4j
@Getter
@Immutable
public class ApiException extends RuntimeException {

    private final transient BaseResponse baseResponse;

    private ApiException(BaseResponse baseResponse) {
        this(baseResponse, baseResponse.getMessage());
    }

    private ApiException(BaseResponse baseResponse, String message) {
        super(message);
        this.baseResponse = baseResponse;
        log.error("[ API EXCEPTION ][ CODE ]:{},[ MESSAGE ]: {}", baseResponse.getCode(), super.getMessage());
    }

    public static ApiException by(BaseResponse baseResponse) {
        return new ApiException(baseResponse);
    }

    public static ApiException by(BaseResponse baseResponse, String errorMsg) {
        return new ApiException(baseResponse, errorMsg);
    }

}
