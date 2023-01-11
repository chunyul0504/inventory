package com.management.inventory.stock.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockResponse {

    private StockDto stock;
    private List<StockDto> stockList;

    public StockResponse(StockDto stockDto) {
        this.stock = stockDto;
    }

    public StockResponse(List<StockDto> stockList) {
        this.stockList = stockList;
    }

}
