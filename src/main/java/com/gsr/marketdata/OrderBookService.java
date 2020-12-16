package com.gsr.marketdata;

import com.gsr.utils.OrderBookUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class OrderBookService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private WebsocketClientEndpoint clientEndPoint;
    public OrderBookService(){
    }

    public void doEnable(){
        connect();
        subscribe();
    }

    private void connect(){
        try {
            // open websocket
            URI coin_base_connection = new URI(OrderBookUtils.COIN_BASE_WEBSOCKET);
            clientEndPoint = new WebsocketClientEndpoint(coin_base_connection);

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println("Received message" + message);
                    processMessage(message);
                }
            });
        } catch (final URISyntaxException ex) {
            String exceptionStr = "Could not connect to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }

    private void subscribe() {
        try {
            // send message to websocket
            clientEndPoint.sendMessage("{\n" +
                    "    \"type\": \"subscribe\",\n" +
                    "    \"product_ids\": [\n" +
                    "        \"BTC-USD\"\n" +
                    "    ],\n" +
                    "    \"channels\": [\n" +
                    "        \"ticker\"\n" +
                    "    ]\n" +
                    "}");
            Thread.currentThread().join();
        } catch (final InterruptedException ex) {
            logger.warning("InterruptedException exception: " + ex.getMessage());
        }
    }

    public void doDisable(){
        clientEndPoint.sendMessage("{\n" +
                "    \"type\": \"unsubscribe\",\n" +
                "    \"product_ids\": [\n" +
                "        \"BTC-USD\"\n" +
                "    ],\n" +
                "    \"channels\": [\n" +
                "        \"level2\"\n" +
                "    ]\n" +
                "}");
    }
    private void processMessage(String message){
        // TODO: add logic to process each Json formatted message string
    }
}
