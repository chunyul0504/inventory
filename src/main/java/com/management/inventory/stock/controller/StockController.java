package com.management.inventory.stock.controller;

import com.management.inventory.exception.ApiException;
import com.management.inventory.response.ApiResponse;
import com.management.inventory.response.StockResponseMessage;
import com.management.inventory.stock.service.StockService;
import com.management.inventory.stock.domain.dto.StockRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ApiResponse getStock(@RequestBody StockRequest stockRequest){
        return ApiResponse.ok(stockService.searchStock(stockRequest));
    }

    @PostMapping("/decrease")
    public ApiResponse decrease(@RequestBody StockRequest stockRequest){
        stockService.quantityManager(stockRequest.decrease());
        return ApiResponse.ok();
    }

    @PostMapping("/increase")
    public ApiResponse increase(@RequestBody StockRequest stockRequest){
        stockService.quantityManager(stockRequest);
        return ApiResponse.ok();
    }

    @GetMapping("/test/success")
    public ApiResponse test(){
        return ApiResponse.ok();
    }

    @GetMapping("/test/fail")
    public ApiResponse testFail(){
        throw ApiException.by(StockResponseMessage.STOCK_FAIL_REGISTER);
    }

    @GetMapping("/test/exception")
    public ApiResponse testException() throws Exception {
        throw new Exception("강제 오류 발생");
    }

}
