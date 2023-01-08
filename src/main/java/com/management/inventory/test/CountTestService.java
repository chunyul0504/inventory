package com.management.inventory.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountTestService {

    private final CountTestRepository countTestRepository;

    public void quantityManagement(Long seq, Long count){

        String threadName = Thread.currentThread().getName();

        CountTest countTest = countTestRepository.findById(seq).orElseThrow();

        log.info(">>>>> [threadName] : "+ threadName + "[start] : "+ countTest.toString());

        countTest.quantityManagement(count);
        countTestRepository.saveAndFlush(countTest);

        log.info(">>>>> [threadName] : "+ threadName + "[end] : "+ countTest.toString());

    }

}
