package com.gsr.marketdata;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import static com.gsr.utils.OrderBookUtils.*;

public class OrderBookService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private WebSocketClientEndpoint clientEndPoint;
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
            clientEndPoint = new WebSocketClientEndpoint(coin_base_connection);

            // add listener
            clientEndPoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
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
            String subscribeJsonMessage = constructJsonMessage(Type.SUBSCRIBE, instrument, Channel.L2);
            // send message to websocket
            clientEndPoint.sendMessage(subscribeJsonMessage);

            Thread.currentThread().join();
        } catch (final InterruptedException ex) {
            String exceptionStr = "Could not subscribe to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }

    public void doDisable(){
        try {
            clientEndPoint.disconnect();
            String unsubscrwibeJsonMessage = constructJsonMessage(Type.UNSUBSCRIBE, instrument, Channel.L2);
            clientEndPoint.sendMessage(unsubscrwibeJsonMessage);
        }
        catch(final IOException ex){
            String exceptionStr = "Could not disconnect to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }

    private void processMessage(String message){
        // TODO: add logic to process each Json formatted message string
    }
}
