package com.management.inventory.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String code;
    private int state;
    private String message;
    private T data = null;

    public ApiResponse<T> data(T data) {
        this.data = data;
        return this;
    }

    public ApiResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    protected ApiResponse(BaseResponse baseResponse) {
        this(baseResponse.getCode(), baseResponse.getStatus(), baseResponse.getMessage(), null);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(HttpResponseMessage.SUCCESS);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(HttpResponseMessage.SUCCESS).data(data);
    }

    /**
     * 예외케이스에 대한 내용은 ApiException.by(); 로 이용 할 것.
     */
    public static ApiResponse<String> fail(BaseResponse baseResponse) {
        return new ApiResponse<>(baseResponse);
    }

    public static <T> ApiResponse<T> error(String eString) {
        return new ApiResponse<T>(HttpResponseMessage.BAD_REQUEST).message(eString);
    }

    public static <T> ApiResponse<T> fail(BaseResponse baseResponse, String failMessage) {
        return new ApiResponse<T>(baseResponse).message(failMessage);
    }

}
