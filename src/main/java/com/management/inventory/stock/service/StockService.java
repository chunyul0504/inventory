package com.management.inventory.stock.service;

import com.management.inventory.stock.domain.entity.Stock;
import com.management.inventory.stock.domain.dto.StockDto;
import com.management.inventory.stock.domain.dto.StockRequest;
import com.management.inventory.stock.domain.dto.StockResponse;
import com.management.inventory.stock.domain.repository.StockRepository;
import com.management.inventory.stock.domain.repository.StockRepositorySupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class StockService {

    private final RedissonClient redissonClient;
    private final StockRepository stockRepository;
    private final StockRepositorySupport stockRepositorySupport;

    /**
     * 재고 수량 조회
     * @param stockRequest
     * @return StockResponse
     */
    public StockResponse searchStock(StockRequest stockRequest){
        if(stockRequest.optionIsBlank()){
            return new StockResponse(this.findStockList(stockRequest.getProductName()));
        }else{
            return new StockResponse(this.findStock(stockRequest.getProductName(), stockRequest.getOptionName()));
        }
    }

    /**
     * 상품명 + 옵션명 = (재고수량)
     * @param productName
     * @param optionName
     * @return
     */
    private StockDto findStock(String productName, String optionName){
        return stockRepositorySupport.findStock(productName, optionName);
    }

    /**
     * 상품명 = (옵션 + 재고수량)
     * @param productName
     * @return
     */
    private List<StockDto> findStockList(String productName){
        return stockRepositorySupport.findStockList(productName);
    }

    public void quantityManager(StockRequest stockRequest){
        Long seq = stockRepository.findByProductNameAndOptionName(stockRequest.getProductName(), stockRequest.getOptionName());
        this.stockUpdate(seq, stockRequest.getQuantity());
    }

    /**
     * 재고 수량 업데이트
     * @param seq
     * @param quantity
     */
    public void stockUpdate(final Long seq, final Long quantity) {
        //key 로 Lock 객체 가져옴
        RLock lock = redissonClient.getLock(seq.toString());

        try {
            //획득시도 시간, 락 점유 시간
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("lock 획득 실패");
                return;
            }
            Stock stock = stockRepository.findById(seq).orElseThrow();
            stock.quantityManagement(quantity);
            stockRepository.saveAndFlush(stock);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
