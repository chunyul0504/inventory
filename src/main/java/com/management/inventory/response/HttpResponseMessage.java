package com.management.inventory.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpResponseMessage implements BaseResponse {

    SUCCESS(200, "정상"),
    ERROR(500, "시스템 오류"),
    BAD_REQUEST(400, "잘못된 요청"),
    UNAUTHORIZED(401, "권한이 없습니다."),
    NOT_FOUND(404, "요청하신 정보를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않는 메소드");

    private final String code = name();
    private final int status;
    private final String message;

}
