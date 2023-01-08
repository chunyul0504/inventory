package com.management.inventory.response;

public interface BaseResponse {
    /* 응답코드 */
    String getCode();

    /* HTTP 상태코드 */
    int getStatus();

    /* 응답 메세지 */
    String getMessage();

    static BaseResponse by(String code, int status, String message) {
        return new BaseResponse() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public int getStatus() {
                return status;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

}
