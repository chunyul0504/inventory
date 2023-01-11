package com.management.inventory.stock.controller;

import com.management.inventory.response.ApiResponse;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ApiResponse getStock(@RequestBody StockRequest stockRequest) {
        return ApiResponse.ok(stockService.searchStock(stockRequest));
    }

    @PostMapping("/increase")
    public ApiResponse increase(@RequestBody StockRequest stockRequest) {
        stockService.quantityManager(stockRequest.increase());
        return ApiResponse.ok();
    }

    @PostMapping("/decrease")
    public ApiResponse decrease(@RequestBody StockRequest stockRequest) {
        stockService.quantityManager(stockRequest.decrease());
        return ApiResponse.ok();
    }


}
