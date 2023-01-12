package com.management.inventory.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockResponseMessage implements BaseResponse {

    STOCK_FAIL_SEARCH(400, "재고 조회에 실패했습니다."),
    STOCK_FAIL_UPDATE(400, "재고 업데이트에 실패했습니다."),
    STOCK_FAIL_REGISTER(400, "재고 등록에 실패했습니다."),
    INVALID_PRODUCT_NAME_VALUE(400, "상품명이 잘못되었습니다."),
    INVALID_OPTION_NAME_VALUE(400, "옵션명이 잘못되었습니다."),
    INVALID_QUANTITY_VALUE(400, "재고수량이 잘못되었습니다."),
    NO_STOCK(400, "재고가 0개 미만입니다.");

    private final String code = name();
    private final int status;
    private final String message;

}
