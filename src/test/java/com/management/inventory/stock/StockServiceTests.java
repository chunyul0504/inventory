package com.management.inventory.stock;

import com.management.inventory.stock.domain.dto.StockDto;
import com.management.inventory.stock.domain.entity.Stock;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.domain.dto.StockResponse;
import com.management.inventory.stock.domain.repository.StockRepository;
import com.management.inventory.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class StockServiceTests {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;

    // 테스트 시 기초 database set
    @BeforeEach
    public void before(){

        List<Stock> stockList = new LinkedList<>();
        stockList.add(new Stock(1L, "prd-a", "opt-aa", 0L));
        stockList.add(new Stock(2L, "prd-a", "opt-ab", 0L));
        stockList.add(new Stock(3L, "prd-b", "opt-ba", 0L));
        stockList.add(new Stock(4L, "prd-b", "opt-bb", 0L));
        stockList.add(new Stock(5L, "prd-b", "opt-bc", 0L));
        stockList.add(new Stock(6L, "prd-c", "opt-cc", 0L));

        stockRepository.saveAllAndFlush(stockList);
    }

    // 테스트 종료 시 database delete
    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }

    @DisplayName("상품명과 옵션명을 넣으면 조합에 해당하는 재고수량을 반환한다.")
    @Test
    public void productOptionStocks() {
        StockRequest stockRequest = new StockRequest("prd-a", "opt-aa");
        StockResponse stockResponse = stockService.searchStock(stockRequest);
        assertEquals(0L, stockResponse.getStock().getQuantity());
    }

    @DisplayName("상품명에 따라 해당하는 옵션별 재고수량을 반환한다.")
    @ParameterizedTest
    @ValueSource( strings = {"prd-b", "prd-c"})
    public void productStocks(String productName) {
        StockRequest stockRequest = new StockRequest(productName);
        StockResponse stockResponse = stockService.searchStock(stockRequest);
        log.info(">>>>::::: stockResponse :::::>>>> {}", stockResponse.toString());
        assertEquals(2L, stockResponse.getStockList().size());

        List<StockDto> resultDtoList = stockResponse.getStockList();
        List<String> productNames = resultDtoList.stream().map(item -> item.getProductName()).collect(Collectors.toList());
        assertTrue(true, String.valueOf(productNames.contains("opt-ba")));

    }

    /**
     * 재고 증가 테스트
     */
    @Test
    public void stockQuantityManagementIncrease() {
        stockService.stockUpdate(1L, 1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(1, stock.getQuantity());
    }

    /**
     * 재고 감소 테스트
     */
    @Test
    public void stockQuantityManagementDecrease() {
        stockService.stockUpdate(1L, -1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity());
    }


//
//    @Test
//    public void multiThreadTest() throws InterruptedException {
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//
//                try {
//                    stockService.stockUpdate(1L, 1L);
//                } catch (Exception e) {
//                    throw new RuntimeException();
//                } finally {
//                    countDownLatch.countDown();
//                }
//
//            });
//        }
//
//        countDownLatch.await();
//
//        Stock stock = stockRepository.findById(1L).orElseThrow();
//
////        assertEquals(0L, stock.getQuantity());
//    }




}