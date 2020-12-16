package com.gsr.marketdata;
import java.util.logging.Logger;

public class MainRunner {
    private static final Logger logger = Logger.getLogger(MainRunner.class.getName());

    public static void main(String[] args) {
        String instrument = "";
        if(args.length != 1){
            logger.warning("Invalid input. Expecting 1 instrument to subscribe to for market data");
            System.exit(0);
        }
        else{
            instrument = args[0];
        }
        OrderBookService orderBookService = new OrderBookService(instrument);
        orderBookService.doEnable();

//        Thread.sleep(2000);
        orderBookService.doDisable();
    }
}
