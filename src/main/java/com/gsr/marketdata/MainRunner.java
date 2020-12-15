package com.gsr.marketdata;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class MainRunner {
    private static final Logger logger = Logger.getLogger(MainRunner.class.getName());
    private static CountDownLatch latch;
    public static void main(String[] args) throws InterruptedException {
        OrderBookService orderBookService = new OrderBookService();
        orderBookService.doEnable();

        Thread.sleep(2000);
        orderBookService.doDisable();
    }
}
