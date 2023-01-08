package com.management.inventory.stock.domain.dto;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StockRequest {

    private String productName;
    private String optionName;
    private Long quantity;

    public StockRequest(String productName, String optionName) {
        this.productName = productName;
        this.optionName = optionName;
    }

    public StockRequest(String productName, String optionName, Long quantity) {
        this.productName = productName;
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public StockRequest(String productName) {
        this.productName = productName;
    }

    public boolean optionIsBlank(){
        if(StringUtils.isBlank(optionName)){
            return true;
        }
        return false;
    }

    public StockRequest decrease(){
        this.quantity = - this.quantity;
        return this;
    }

}
