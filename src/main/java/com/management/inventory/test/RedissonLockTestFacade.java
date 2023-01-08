package com.management.inventory.test;

import com.management.inventory.test.CountTestService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockTestFacade {

    private final RedissonClient redissonClient;
    private final CountTestService testService;

    public void quantityManagement(final Long key, final Long quantity) {
        //key 로 Lock 객체 가져옴
        RLock lock = redissonClient.getLock(key.toString());

        try {
            //획득시도 시간, 락 점유 시간
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }
            testService.quantityManagement(key, quantity);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
