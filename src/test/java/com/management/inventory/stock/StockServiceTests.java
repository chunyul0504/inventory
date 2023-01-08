package com.management.inventory.stock;

import com.management.inventory.stock.domain.entity.Stock;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.domain.dto.StockResponse;
import com.management.inventory.stock.domain.repository.StockRepository;
import com.management.inventory.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class StockServiceTests {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;

//    @BeforeEach
//    public void before(){
//        Stock stock = new Stock(1L, 100L);
//        stockRepository.saveAndFlush(stock);
//    }
//
//    @AfterEach
//    public void after(){
//        stockRepository.deleteAll();
//    }

    @Test
    public void stockQuantityManagementDecrease() {
        stockService.stockUpdate(1L, -1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(99, stock.getQuantity());
    }

    @Test
    public void stockQuantityManagementIncrease() {
        stockService.stockUpdate(1L, 1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(101, stock.getQuantity());
    }

    @Test
    public void multiThreadTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {

                try {
                    stockService.stockUpdate(1L, 1L);
                } catch (Exception e) {
                    throw new RuntimeException();
                } finally {
                    countDownLatch.countDown();
                }

            });
        }

        countDownLatch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

//        assertEquals(0L, stock.getQuantity());
    }

    @Test
    public void productOptionStocks() {
        StockRequest stockRequest = new StockRequest("prd-a", "opt-aa");
        StockResponse stockResponse = stockService.searchStock(stockRequest);
        log.info(">>>>::::: stockResponse :::::>>>> {}", stockResponse.toString());
//        assertEquals(0L, stockDto.getQuantity());
    }

    @Test
    public void productStocks() {
        StockRequest stockRequest = new StockRequest("prd-a");
        StockResponse stockResponse = stockService.searchStock(stockRequest);
        log.info(">>>>::::: stockResponse :::::>>>> {}", stockResponse.toString());
//        assertEquals(0L, stockDto.getQuantity());
    }

}