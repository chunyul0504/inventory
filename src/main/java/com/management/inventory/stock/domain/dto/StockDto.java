package com.management.inventory.stock.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.management.inventory.stock.domain.entity.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockDto {

    private String productName;
    private String optionName;
    private Long quantity;

    @Builder
    public StockDto(Stock stock) {
        this.productName = stock.getProductName();
        this.optionName = stock.getOptionName();
        this.quantity = stock.getQuantity();
    }


}
