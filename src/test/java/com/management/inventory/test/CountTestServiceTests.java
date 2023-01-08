package com.management.inventory.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CountTestServiceTests {

    @Autowired
    private CountTestRepository countTestRepository;
    @Autowired
    private RedissonLockTestFacade redissonLockTestFacade;

//    @BeforeEach
//    public void insert() {
//        CountTest countTest = new CountTest(1L, 0L);
//        countTestRepository.saveAndFlush(countTest);
//    }
//
//    @AfterEach
//    public void delete() {
//        countTestRepository.deleteAll();
//    }

    @Test
    public void countTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int tc = 0; tc < threadCount; tc++) {
            executorService.submit(() -> {
                try {
                    redissonLockTestFacade.quantityManagement(1L, 1L);
                    Thread.sleep(2000);
                } catch (Exception e) {
                    throw new RuntimeException();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        CountTest countTest = countTestRepository.findById(1L).orElseThrow();
        System.out.println("countTest : " + countTest.toString());
//        assertEquals(10L, countTest.getCount());

    }


}
