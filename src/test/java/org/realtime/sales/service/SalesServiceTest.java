package org.realtime.sales.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalesServiceTest {

    @Autowired
    private SalesService salesService;

    @Autowired
    private SalesManageService salesManageService;

    @DisplayName("동시에 100개 요청")
    @Test
    void updateSalesWithHincrbyWithoutSync() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                salesService.updateSalesWithHincrby("34242411", "CARD", 5000, "A");
            });
        }

        // 모든 스레드가 끝날 때까지 기다리지 않고 바로 종료하도록 수정
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(100); // 약간의 대기
        }
    }

//    @AfterEach
//    void cleanupRedis(){
//        salesManageService.cleanUpRedis("34242411");
//    }

}