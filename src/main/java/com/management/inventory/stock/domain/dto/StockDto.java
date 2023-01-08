package com.management.inventory.stock.domain.dto;

import com.management.inventory.stock.domain.entity.Stock;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StockDto {

    private Long seq;
    private String productName;
    private String optionName;
    private Long quantity;

    @Builder
    public StockDto(Stock stock) {
        this.seq = stock.getSeq();
        this.productName = stock.getProductName();
        this.optionName = stock.getOptionName();
        this.quantity = stock.getQuantity();
    }


}
