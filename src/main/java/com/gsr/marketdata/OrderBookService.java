package com.gsr.marketdata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static com.gsr.utils.OrderBookUtils.*;

public class OrderBookService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private WebsocketClientEndpoint clientEndPoint;
    private String instrument;

    public OrderBookService(String instrument){
        this.instrument = instrument;
    }

    public void doEnable(){
        connect();
        subscribe();
    }

    private void connect(){
        try {
            // open websocket
            URI coin_base_connection = new URI(COIN_BASE_WEBSOCKET);
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
            String subscribeJsonMessage = constructLevel2JsonMessageWithType(Type.SUBSCRIBE, instrument, Channel.L2);
            // send message to websocket
            clientEndPoint.sendMessage(subscribeJsonMessage);

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
