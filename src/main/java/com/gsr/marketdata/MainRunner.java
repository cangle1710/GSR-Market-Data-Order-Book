package com.gsr.marketdata;

import java.util.logging.Logger;

public class MainRunner {

    private static final Logger logger = Logger.getLogger(MainRunner.class.getName());

    public static void main(String[] args){
        OrderBookService orderBookService = new OrderBookService();
        orderBookService.connect();
    }
}
