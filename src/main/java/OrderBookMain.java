

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class OrderBookMain {

    private static final Logger logger = Logger.getLogger(OrderBookMain.class.getName());

    public static void main(String[] args){
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(OrderBookUtils.COIN_BASE_WEBSOCKET));

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
