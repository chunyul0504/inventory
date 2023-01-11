package com.management.inventory.stock.service;

import com.management.inventory.exception.ApiException;
import com.management.inventory.response.StockResponseMessage;
import com.management.inventory.stock.domain.dto.StockDto;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.domain.dto.StockResponse;
import com.management.inventory.stock.domain.entity.Stock;
import com.management.inventory.stock.domain.repository.StockRepository;
import com.management.inventory.stock.domain.repository.StockRepositorySupport;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StockService {

    private final RedissonClient redissonClient;
    private final StockRepository stockRepository;
    private final StockRepositorySupport stockRepositorySupport;

    public StockService(RedissonClient redissonClient, StockRepository stockRepository, StockRepositorySupport stockRepositorySupport) {
        this.redissonClient = redissonClient;
        this.stockRepository = stockRepository;
        this.stockRepositorySupport = stockRepositorySupport;
    }

    /**
     * 재고 수량 조회
     *
     * @param stockRequest
     * @return StockResponse
     */
    public StockResponse searchStock(StockRequest stockRequest) {
        if (stockRequest.productNameIsBlank()) {
            throw ApiException.by(StockResponseMessage.INVALID_REQUIRED_VALUE);
        }

        if (stockRequest.optionNameIsBlank()) {
            return new StockResponse(this.findStockList(stockRequest.getProductName()));
        } else {
            return new StockResponse(this.findStock(stockRequest.getProductName(), stockRequest.getOptionName()));
        }
    }

    /**
     * 상품명 + 옵션명 = (재고수량)
     *
     * @param productName
     * @param optionName
     * @return
     */
    private StockDto findStock(String productName, String optionName) {
        return stockRepositorySupport.findStock(productName, optionName);
    }

    /**
     * 상품명 = (옵션 + 재고수량)
     *
     * @param productName
     * @return
     */
    private List<StockDto> findStockList(String productName) {
        return stockRepositorySupport.findStockList(productName);
    }

    public void quantityManager(StockRequest stockRequest) {
        Long seq = stockRepositorySupport.findStockSeq(stockRequest.getProductName(), stockRequest.getOptionName());
        this.stockUpdate(seq, stockRequest.getQuantity());
    }

    /**
     * 재고 수량 업데이트
     *
     * @param seq
     * @param quantity
     */
    private void stockUpdate(final Long seq, final Long quantity) {

        RLock lock = redissonClient.getLock(seq.toString());

        try {

            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("lock 획득을 실패했습니다. 실패 SEQ : {}", seq);
                return;
            }

            Stock stock = stockRepository.findById(seq).orElseThrow();
            stock.quantityManagement(quantity);
            stockRepository.saveAndFlush(stock);

        } catch (InterruptedException e) {
            throw ApiException.by(StockResponseMessage.STOCK_FAIL_UPDATE, e.toString());
        } catch (ApiException e) {
            throw ApiException.by(e.getBaseResponse());
        } finally {
            lock.unlock();
        }
    }

}
