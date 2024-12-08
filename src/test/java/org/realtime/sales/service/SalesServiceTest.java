package org.realtime.sales.service;

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

    @DisplayName("동시에 100개 요청")
    @Test
    void updateSalesWithHincrby() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i =0; i < threadCount; i++) {
            executorService.submit(()-> {
                try {
                    salesService.updateSalesWithHincrby("34242411", "CARD", 5000, "A");
                } finally{
                    latch.countDown();
                }
            });
        }

        latch.await();

    }
}