package com.gsr.marketdata;

import com.gsr.utils.OrderBookUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class OrderBookService {
    private final Logger logger = Logger.getLogger(MainRunner.class.getName());
    private WebsocketClientEndpoint clientEndPoint;
    public OrderBookService(){ }

    public void connect(){
        try {
            // open websocket
            URI coin_base_connection = new URI(OrderBookUtils.COIN_BASE_WEBSOCKET);
            clientEndPoint = new WebsocketClientEndpoint(coin_base_connection);

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println("Received message" + message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{\n" +
                    "    \"type\": \"subscribe\",\n" +
                    "    \"product_ids\": [\n" +
                    "        \"BTC-USD\"\n" +
                    "    ],\n" +
                    "    \"channels\": [\n" +
                    "        \"level2\"\n" +
                    "    ]\n" +
                    "}");
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            logger.warning("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            logger.warning("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
