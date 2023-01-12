package com.management.inventory.stock;

import com.management.inventory.exception.ApiException;
import com.management.inventory.response.StockResponseMessage;
import com.management.inventory.stock.domain.dto.StockDto;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.domain.dto.StockResponse;
import com.management.inventory.stock.domain.entity.Stock;
import com.management.inventory.stock.domain.repository.StockRepository;
import com.management.inventory.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class StockServiceUnitTests {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;

    // 테스트 시 기초 database set
    @BeforeEach
    public void before() {

        List<Stock> stockList = new LinkedList<>();
        stockList.add(new Stock(1L, "prd-a", "opt-aa", 0L));
        stockList.add(new Stock(2L, "prd-a", "opt-ab", 0L));
        stockList.add(new Stock(3L, "prd-b", "opt-ba", 0L));
        stockList.add(new Stock(4L, "prd-b", "opt-bb", 0L));
        stockList.add(new Stock(5L, "prd-b", "opt-bc", 0L));
        stockList.add(new Stock(6L, "prd-c", "opt-ca", 0L));

        stockRepository.saveAllAndFlush(stockList);
    }

    // 테스트 종료 시 database delete
    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }


    // 상품명 조회에 사용
    static Stream<Arguments> stockProductProvider() {
        return Stream.of(
                Arguments.of("prd-a", Arrays.asList("opt-aa", "opt-ab"), Arrays.asList(0L, 0L)),
                Arguments.of("prd-b", Arrays.asList("opt-ba", "opt-bb", "opt-bc"), Arrays.asList(0L, 0L, 0L)),
                Arguments.of("prd-c", Arrays.asList("opt-ca"), Arrays.asList(0L))
        );
    }


    @DisplayName("상품명에 따라 해당하는 옵션별 재고수량을 반환한다.")
    @ParameterizedTest
    @MethodSource("stockProductProvider")
    public void productStocks(String productName, List<String> optionName, List<Long> quantity) {

        StockRequest stockRequest =
                StockRequest
                        .builder()
                        .productName(productName)
                        .build();

        StockResponse stockResponse = stockService.searchStock(stockRequest);

        assertTrue(0 < stockResponse.getStockList().size());

        for (StockDto stockDto : stockResponse.getStockList()) {
            assertTrue(optionName.contains(stockDto.getOptionName()));
        }

    }

    // 상품명+옵션명 조회에 사용
    static Stream<Arguments> stockProductOptionsProvider() {
        return Stream.of(
                Arguments.of("prd-a", "opt-aa", 0L),
                Arguments.of("prd-a", "opt-ab", 0L),
                Arguments.of("prd-b", "opt-ba", 0L),
                Arguments.of("prd-b", "opt-bb", 0L),
                Arguments.of("prd-b", "opt-bc", 0L),
                Arguments.of("prd-c", "opt-ca", 0L)
        );
    }

    @DisplayName("상품명과 옵션명을 넣으면 조합에 해당하는 재고수량을 반환한다.")
    @ParameterizedTest
    @MethodSource("stockProductOptionsProvider")
    public void productOptionStocks(String productName, String optionName, Long quantity) {

        StockRequest stockRequest =
                StockRequest
                        .builder()
                        .productName(productName)
                        .optionName(optionName)
                        .build();

        StockResponse stockResponse = stockService.searchStock(stockRequest);

        assertEquals(productName, stockResponse.getStock().getProductName());
        assertEquals(optionName, stockResponse.getStock().getOptionName());
        assertEquals(quantity, stockResponse.getStock().getQuantity());
    }

    // 재고 증감소 테스트에 사용
    static Stream<Arguments> stockUpdateProvider() {
        return Stream.of(
                Arguments.of("prd-a", "opt-aa", 1L),
                Arguments.of("prd-a", "opt-ab", 1L),
                Arguments.of("prd-b", "opt-ba", 1L),
                Arguments.of("prd-b", "opt-bb", 1L),
                Arguments.of("prd-b", "opt-bc", 1L),
                Arguments.of("prd-c", "opt-ca", 1L)
        );
    }

    /**
     * 재고 증가 테스트
     */
    @DisplayName("상품명, 옵션명과 함께 증가시키고 싶은 수량을 입력하면 증가된다.")
    @ParameterizedTest
    @MethodSource("stockUpdateProvider")
    public void stockQuantityManagementIncrease(String productName, String optionName, Long quantity) {

        StockRequest stockRequest =
                StockRequest
                        .builder()
                        .productName(productName)
                        .optionName(optionName)
                        .quantity(quantity)
                        .build();

        StockResponse beforeStockResponse = stockService.searchStock(stockRequest);
        stockService.quantityManager(stockRequest.increase());
        StockResponse afterStockResponse = stockService.searchStock(stockRequest);

        assertEquals(productName, afterStockResponse.getStock().getProductName());
        assertEquals(optionName, afterStockResponse.getStock().getOptionName());
        assertEquals(quantity + beforeStockResponse.getStock().getQuantity(), afterStockResponse.getStock().getQuantity());
    }

    /**
     * 재고 감소 테스트
     */
    @DisplayName("재고 감소 요청시 0 아래로 떨어지면 예외처리.")
    @ParameterizedTest
    @MethodSource("stockUpdateProvider")
    public void stockQuantityManagementDecrease(String productName, String optionName, Long quantity) {

        StockRequest stockRequest =
                StockRequest
                        .builder()
                        .productName(productName)
                        .optionName(optionName)
                        .quantity(quantity)
                        .build();

        ApiException apiException = assertThrows(ApiException.class, () -> {
            stockService.quantityManager(stockRequest.decrease());
        });
        String message = apiException.getMessage();
        assertEquals("재고가 0개 미만입니다.", message);
    }


    /**
     * 재고 증가 후 감소 테스트
     */
    @DisplayName("상품명, 옵션명과 함께 감소시키고 싶은 수량을 입력하면 감소된다.")
    @ParameterizedTest
    @MethodSource("stockUpdateProvider")
    public void stockQuantityUpdate(String productName, String optionName, Long quantity) {

        StockRequest stockRequest =
                StockRequest
                        .builder()
                        .productName(productName)
                        .optionName(optionName)
                        .quantity(quantity)
                        .build();

        // 재고 증가
        StockResponse beforeStockIncrease = stockService.searchStock(stockRequest);
        stockService.quantityManager(stockRequest.increase());
        StockResponse afterStockIncrease = stockService.searchStock(stockRequest);

        assertEquals(productName, afterStockIncrease.getStock().getProductName());
        assertEquals(optionName, afterStockIncrease.getStock().getOptionName());
        assertEquals(quantity + beforeStockIncrease.getStock().getQuantity(), afterStockIncrease.getStock().getQuantity());

        // 재고 감소
        StockResponse beforeStockDecrease = stockService.searchStock(stockRequest);
        stockService.quantityManager(stockRequest.decrease());
        StockResponse afterStockDecrease = stockService.searchStock(stockRequest);

        assertEquals(productName, afterStockDecrease.getStock().getProductName());
        assertEquals(optionName, afterStockDecrease.getStock().getOptionName());
        assertEquals(beforeStockDecrease.getStock().getQuantity() - quantity, afterStockDecrease.getStock().getQuantity());
    }

    @DisplayName("멀티스레드 환경에서의 증가 테스트. 최종 갯수와 일치하는지 확인 하는 테스트.")
    @ParameterizedTest
    @MethodSource("stockUpdateProvider")
    public void multiThreadIncreaseTest(String productName, String optionName, Long quantity) throws InterruptedException {

        StockRequest stockRequest =
                StockRequest
                        .builder()
                        .productName(productName)
                        .optionName(optionName)
                        .quantity(quantity)
                        .build();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {

                try {
                    stockService.quantityManager(stockRequest.increase());
                } catch (Exception e) {
                    throw ApiException.by(StockResponseMessage.STOCK_FAIL_UPDATE);
                } finally {
                    // countDownLatch 숫자 감소
                    countDownLatch.countDown();
                }

            });
        }

        // countDownLatch 의 숫자가 0이 될 때까지 대기
        countDownLatch.await();

        StockResponse stockResponse = stockService.searchStock(StockRequest.builder().productName(productName).optionName(optionName).build());

        assertEquals(productName, stockResponse.getStock().getProductName());
        assertEquals(optionName, stockResponse.getStock().getOptionName());
        assertEquals(100L, stockResponse.getStock().getQuantity());

    }

    @DisplayName("멀티스레드 환경에서의 감소 테스트. 최종 갯수와 일치하는지 확인 하는 테스트.")
    @ParameterizedTest
    @MethodSource("stockUpdateProvider")
    public void multiThreadDecreaseTest(String productName, String optionName, Long quantity) throws InterruptedException {

        // 재고 0개로 default 생성되기 때문에 재고 먼저 100개로 증가시켜준다
        Long increaseCount = 100L;

        StockRequest stockRequestIncrease =
                StockRequest
                        .builder()
                        .productName(productName)
                        .optionName(optionName)
                        .quantity(increaseCount) // 재고 증가 최초 100개 생성
                        .build();

        StockResponse beforeStockIncrease = stockService.searchStock(stockRequestIncrease);
        stockService.quantityManager(stockRequestIncrease.increase());
        StockResponse afterStockIncrease = stockService.searchStock(stockRequestIncrease);

        assertEquals(productName, afterStockIncrease.getStock().getProductName());
        assertEquals(optionName, afterStockIncrease.getStock().getOptionName());
        assertEquals(increaseCount + beforeStockIncrease.getStock().getQuantity(), afterStockIncrease.getStock().getQuantity());

        // 멀티스레드 재고 감소 테스트 시작
        int threadCount = 100;
        // * 참고 : tomcat 의 thread 개수 기본값은 200이다.
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {

            StockRequest stockRequestUpdate =
                    StockRequest
                            .builder()
                            .productName(productName)
                            .optionName(optionName)
                            .quantity(quantity)
                            .build();

            executorService.submit(() -> {


                try {
                    stockService.quantityManager(stockRequestUpdate.decrease());
                } catch (ApiException e) {
                    throw ApiException.by(StockResponseMessage.STOCK_FAIL_UPDATE);
                } finally {
                    // countDownLatch 숫자 감소
                    countDownLatch.countDown();
                }

            });
        }
        // countDownLatch 의 숫자가 0이 될 때까지 대기
        countDownLatch.await();

        StockResponse stockResponse = stockService.searchStock(StockRequest.builder().productName(productName).optionName(optionName).build());
        assertEquals(productName, stockResponse.getStock().getProductName());
        assertEquals(optionName, stockResponse.getStock().getOptionName());
        assertEquals(0L, stockResponse.getStock().getQuantity());

    }


}