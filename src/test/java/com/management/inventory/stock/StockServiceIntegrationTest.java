package com.management.inventory.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.inventory.response.ApiResponse;
import com.management.inventory.stock.domain.dto.StockDto;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.domain.dto.StockResponse;
import com.management.inventory.stock.domain.entity.Stock;
import com.management.inventory.stock.domain.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockServiceIntegrationTest {

    // DB 초기 세팅을 위한 선언
    @Autowired
    private StockRepository stockRepository;

    // 내장
    @LocalServerPort
    private int port;

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

    static Stream<Arguments> stockProductProvider() {
        return Stream.of(
                Arguments.of("prd-a", Arrays.asList("opt-aa", "opt-ab"), Arrays.asList(0L, 0L)),
                Arguments.of("prd-b", Arrays.asList("opt-ba", "opt-bb", "opt-bc"), Arrays.asList(0L, 0L, 0L)),
                Arguments.of("prd-c", Arrays.asList("opt-ca"), Arrays.asList(0L))
        );
    }

    @DisplayName("상품명 조회 후 조회된 옵션별 수량 기준으로 상품명+옵션명 조회하여 기대한 결과 맞는지 확인.")
    @ParameterizedTest
    @MethodSource("stockProductProvider")
    public void productOptionStocks(String productName, List<String> optionName, List<Long> quantity) {

        int TIMEOUT_SEC = 10;
        WebClient webClient = WebClient.create();

        String uri = "http://localhost:" + this.port + "/stock";
        String increasePath = "/increase";
        String decreasePath = "/decrease";

        HttpMethod httpMethodGet = HttpMethod.GET;
        HttpMethod httpMethodPost = HttpMethod.POST;

        ObjectMapper mapper = new ObjectMapper();

        // 상품명 조회 테스트
        StockRequest productStock =
                StockRequest
                        .builder()
                        .productName(productName)
                        .build();

        Mono<ResponseEntity<ApiResponse>> getProductStockResponse
                = webClient
                .method(httpMethodGet)
                .uri(uri)
                .bodyValue(productStock)
                .retrieve()
                .toEntity(ApiResponse.class)
                .timeout(Duration.ofSeconds(TIMEOUT_SEC));

        Map<String, Object> responseGetProductResponse = mapper.convertValue(getProductStockResponse.block().getBody().getData(), Map.class);
        StockResponse stockProductResponse = mapper.convertValue(responseGetProductResponse, StockResponse.class);

        for (StockDto stockDto : stockProductResponse.getStockList()) {
            assertTrue(optionName.contains(stockDto.getOptionName()));

            // 상품명+옵션명 조회 테스트
            StockRequest productOptionStock =
                    StockRequest
                            .builder()
                            .productName(stockDto.getProductName())
                            .optionName(stockDto.getOptionName())
                            .build();

            Mono<ResponseEntity<ApiResponse>> getProductOptionStockResponse
                    = webClient
                    .method(httpMethodGet)
                    .uri(uri)
                    .bodyValue(productOptionStock)
                    .retrieve()
                    .toEntity(ApiResponse.class)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC));

            Map<String, Object> responseMap = mapper.convertValue(getProductOptionStockResponse.block().getBody().getData(), Map.class);
            StockResponse responseGetProductOptionResponse = mapper.convertValue(responseMap, StockResponse.class);

            assertTrue(productName.contains(responseGetProductOptionResponse.getStock().getProductName()));
            assertTrue(optionName.contains(responseGetProductOptionResponse.getStock().getOptionName()));

            // 재고 증감소 count
            Long stockUpdateCount = 1L;

            // 재고 증가 테스트 - 1 증가
            StockRequest increaseStock =
                    StockRequest
                            .builder()
                            .productName(stockDto.getProductName())
                            .optionName(stockDto.getOptionName())
                            .quantity(stockUpdateCount)
                            .build();

            Mono<ResponseEntity<ApiResponse>> increaseStockResponse
                    = webClient
                    .method(httpMethodPost)
                    .uri(uri + increasePath)
                    .bodyValue(increaseStock)
                    .retrieve()
                    .toEntity(ApiResponse.class)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC));

            assertEquals(200, increaseStockResponse.block().getStatusCodeValue());

            // 상품명+옵션명 조회 테스트 : 1이여야함
            StockRequest afterIncrease =
                    StockRequest
                            .builder()
                            .productName(stockDto.getProductName())
                            .optionName(stockDto.getOptionName())
                            .build();

            Mono<ResponseEntity<ApiResponse>> afterIncreaseResponse
                    = webClient
                    .method(httpMethodGet)
                    .uri(uri)
                    .bodyValue(afterIncrease)
                    .retrieve()
                    .toEntity(ApiResponse.class)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC));

            Map<String, Object> afterIncreaseResponseMap = mapper.convertValue(afterIncreaseResponse.block().getBody().getData(), Map.class);
            StockResponse afterIncreaseStockResponse = mapper.convertValue(afterIncreaseResponseMap, StockResponse.class);
            assertTrue(optionName.contains(afterIncreaseStockResponse.getStock().getOptionName()));
            assertTrue(productName.contains(afterIncreaseStockResponse.getStock().getProductName()));
            assertEquals(1L, afterIncreaseStockResponse.getStock().getQuantity());


            // 재고 감소 테스트 - 1 감소
            StockRequest decreaseStock =
                    StockRequest
                            .builder()
                            .productName(stockDto.getProductName())
                            .optionName(stockDto.getOptionName())
                            .quantity(stockUpdateCount)
                            .build();

            Mono<ResponseEntity<ApiResponse>> decreaseStockResponse
                    = webClient
                    .method(httpMethodPost)
                    .uri(uri + decreasePath)
                    .bodyValue(decreaseStock)
                    .retrieve()
                    .toEntity(ApiResponse.class)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC));

            assertEquals(200, decreaseStockResponse.block().getStatusCodeValue());

            // 상품명+옵션명 조회 테스트 : 0 이여야함
            StockRequest afterDecrease =
                    StockRequest
                            .builder()
                            .productName(stockDto.getProductName())
                            .optionName(stockDto.getOptionName())
                            .build();

            Mono<ResponseEntity<ApiResponse>> afterDecreaseResponse
                    = webClient
                    .method(httpMethodGet)
                    .uri(uri)
                    .bodyValue(afterDecrease)
                    .retrieve()
                    .toEntity(ApiResponse.class)
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC));

            Map<String, Object> afterDecreaseResponseMap = mapper.convertValue(afterDecreaseResponse.block().getBody().getData(), Map.class);
            StockResponse afterDecreaseStockResponse = mapper.convertValue(afterDecreaseResponseMap, StockResponse.class);
            assertTrue(optionName.contains(afterDecreaseStockResponse.getStock().getOptionName()));
            assertTrue(productName.contains(afterDecreaseStockResponse.getStock().getProductName()));
            assertEquals(0L, afterDecreaseStockResponse.getStock().getQuantity());

        }

    }

}
