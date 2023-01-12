package com.management.inventory.stock.domain.dto;

import com.management.inventory.exception.ApiException;
import com.management.inventory.response.StockResponseMessage;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Immutable;

import java.util.Objects;

@Getter
@Immutable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockRequest {

    private String productName;
    private String optionName;
    private Long quantity;

    @Builder
    public StockRequest(String productName, String optionName, Long quantity) {
        this.productName = productName;
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public StockRequest(String productName) {
        this.productName = productName;
    }

    public boolean optionNameIsBlank() {
        if (StringUtils.isBlank(optionName)) {
            return true;
        }
        return false;
    }

    public boolean productNameIsBlank() {
        if (StringUtils.isBlank(productName)) {
            return true;
        }
        return false;
    }

    public boolean quantityIsBlank() {
        if (Objects.isNull(quantity) || quantity < 0L) {
            return true;
        }
        return false;
    }

    private void checkQuantityUpdateRequired() {
        if (this.productNameIsBlank()) {
            throw ApiException.by(StockResponseMessage.INVALID_PRODUCT_NAME_VALUE);
        }
        if (this.optionNameIsBlank()) {
            throw ApiException.by(StockResponseMessage.INVALID_OPTION_NAME_VALUE);
        }
        if (this.quantityIsBlank()) {
            throw ApiException.by(StockResponseMessage.INVALID_QUANTITY_VALUE);
        }
    }

    public StockRequest decrease() {
        this.checkQuantityUpdateRequired();
        this.quantity = (-1) * this.quantity;
        return this;
    }

    public StockRequest increase() {
        this.checkQuantityUpdateRequired();
        return this;
    }

}
