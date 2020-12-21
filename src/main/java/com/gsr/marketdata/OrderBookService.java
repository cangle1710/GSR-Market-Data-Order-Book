package com.gsr.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gsr.common.L2OrderResponse;
import com.gsr.common.SnapShotOrderResponse;
import org.javatuples.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

import static com.gsr.common.Constants.*;
import static com.gsr.utils.OrderBookUtils.*;

public class OrderBookService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final int orderBookSize = 10;

    private WebSocketClientEndpoint clientEndPoint;
    private String instrument;
    private Map<Double, Double> bids;
    private Map<Double, Double> asks;
    private PriorityQueue<Double> topOfBidsBook;
    private PriorityQueue<Double> topOfAsksBook;

    public OrderBookService(String instrument){
        this.instrument = instrument;
        this.bids = new HashMap<>();
        this.asks = new HashMap<>();
        this.topOfAsksBook = new PriorityQueue<>((x,y) -> Double.compare(y,x));
        this.topOfBidsBook = new PriorityQueue<>();
    }

    public void doEnable(){
        logger.info("Enabling OrderBookService...");
        connect();
        subscribe();
        logger.info("OrderBookService enabled...");
    }

    private void connect(){
        try {
            // open websocket
            URI coin_base_connection = new URI(COIN_BASE_WEBSOCKET);
            clientEndPoint = new WebSocketClientEndpoint(coin_base_connection);

            // add listener
            clientEndPoint.addMessageHandler(new WebSocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    processMessage(message);
                }
            });

        } catch (final URISyntaxException ex) {
            String exceptionStr = "Could not connect to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }

    private void disconnect(){
        try {
            clientEndPoint.disconnect();
        }
        catch(final IOException ex){
            String exceptionStr = "Could not disconnect to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }
    private void subscribe() {
        try {
            String subscribeJsonMessage = constructJsonMessage(Type.SUBSCRIBE, instrument, Channel.L2);
            // send subscribe message to web socket
            clientEndPoint.sendMessage(subscribeJsonMessage);

            Thread.currentThread().join();
        } catch (final InterruptedException ex) {
            String exceptionStr = "Could not subscribe to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }

    private void unsubscribe(){
        // send unsubscribe message to web socket
        String unsubscribeJsonMessage = constructJsonMessage(Type.UNSUBSCRIBE, instrument, Channel.L2);
        clientEndPoint.sendMessage(unsubscribeJsonMessage);
    }

    public void doDisable(){
        logger.info("Disabling OrderBookService...");
        unsubscribe();
        disconnect();
        logger.info("OrderBookService disabled...");
    }

    private void processMessage(String message){
        final ObjectMapper mapper = new ObjectMapper();

        // adding custom serializer / deserializer for Json parsing
        final SimpleModule module = new SimpleModule();
        module.addSerializer(Pair.class, new ResponseSerializer());
        module.addDeserializer(Pair.class, new ResponseDeserializer());
        mapper.registerModule(module);

        try{
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(message);
            String type = (String) json.get(TYPE);
            if(type.equals(SNAPSHOT)){
                SnapShotOrderResponse snapShotResponse = mapper.readValue(json.toString(), SnapShotOrderResponse.class);
                buildOrderBook(snapShotResponse);
            }
            else if(type.equals(L2_UPDATE)){
                L2OrderResponse l2Response = mapper.readValue(json.toString(), L2OrderResponse.class);
                updateOrderBook(l2Response);
            }
            System.out.println(message);
        } catch(final ParseException | IOException ex){
            String exceptionStr = "Could not process web socket response : " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
         }
    }

    private void buildOrderBook(SnapShotOrderResponse snapShotOrderResponse){
        List<Pair<String, String>> asksList = snapShotOrderResponse.getAsks();
        for(Pair<String, String> pair : asksList){
            double price = Double.parseDouble(pair.getValue0());
            double size = Double.parseDouble(pair.getValue1());

            asks.put(price, size);
            topOfAsksBook.offer(price);
            if(topOfAsksBook.size() > orderBookSize){
                topOfAsksBook.poll();
            }
        }

        List<Pair<String, String>> bidsList = snapShotOrderResponse.getBids();
        for(Pair<String, String> pair : bidsList){
            double price = Double.parseDouble(pair.getValue0());
            double size = Double.parseDouble(pair.getValue1());

            bids.put(price, size);
            topOfBidsBook.offer(price);
            if(topOfBidsBook.size() > orderBookSize){
                topOfBidsBook.poll();
            }
        }
    }

    // {"type":"l2update","product_id":"BTC-USD","changes":[["sell","22949.81","0.10000000"]],"time":"2020-12-21T21:34:32.969574Z"}

    private void updateOrderBook(L2OrderResponse l2Response){
        List<Pair<String, Pair<String, String>>> l2List = l2Response.getChanges();
        for(Pair<String, Pair<String, String>> pair : l2List){
            String side = pair.getValue0();
            Pair<String, String> tuple = pair.getValue1();
            double price = Double.parseDouble(tuple.getValue0());
            double size = Double.parseDouble(tuple.getValue1());

            // delete operation - price is 0.00
            // remove element from map
            if(size == 0.0){

            }

        }
    }

    private void updateBook(Map<Double, Double> bookMap, PriorityQueue<Double> bookQueue, double price, double size){

    }
}
