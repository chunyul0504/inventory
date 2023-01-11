package com.management.inventory.stock.domain.dto;

import com.management.inventory.exception.ApiException;
import com.management.inventory.response.StockResponseMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Getter
@NoArgsConstructor
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

    private boolean checkQuantityUpdateRequired() {

        if (this.productNameIsBlank() || this.optionNameIsBlank() || this.quantityIsBlank()) {
            throw ApiException.by(StockResponseMessage.INVALID_REQUIRED_VALUE);
        }
        return true;
    }

    public StockRequest decrease() {
        this.checkQuantityUpdateRequired();
        this.quantity = -this.quantity;
        return this;
    }

    public StockRequest increase() {
        this.checkQuantityUpdateRequired();
        return this;
    }

}
