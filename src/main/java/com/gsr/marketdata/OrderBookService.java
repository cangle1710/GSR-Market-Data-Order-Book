package com.gsr.marketdata;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsr.common.L2Changes;
import com.gsr.common.OrderOffer;
import com.gsr.common.Wrapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

import static com.gsr.utils.OrderBookUtils.*;

public class OrderBookService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private WebSocketClientEndpoint clientEndPoint;
    private String instrument;
    private Map<Double, Double> bids;
    private Map<Double, Double> asks;

    public OrderBookService(String instrument){
        this.instrument = instrument;
        this.bids = new HashMap<>();
        this.asks = new HashMap<>();
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
            // send message to websocket
            clientEndPoint.sendMessage(subscribeJsonMessage);

            Thread.currentThread().join();
        } catch (final InterruptedException ex) {
            String exceptionStr = "Could not subscribe to Coinbase websocket: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            logger.warning(exceptionStr);
        }
    }

    private void unsubscribe(){
        String unsubscribeJsonMessage = constructJsonMessage(Type.UNSUBSCRIBE, instrument, Channel.L2);
        clientEndPoint.sendMessage(unsubscribeJsonMessage);
    }

    public void doDisable(){
        unsubscribe();
        disconnect();
    }

    private void processMessage(String message){
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(message);
        try{
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(message);
            String type = (String) json.get("type");
            if(type.equals("snapshot")){
                JSONArray asksJsonArray = (JSONArray) json.get("asks");
                List<OrderOffer> asksList;
                if(asksJsonArray != null){
                    Wrapper wrapper = mapper.readValue(asksJsonArray.toString(), Wrapper.class);
//                    OrderOffer[] oo1 = mapper.readValue(asksJsonArray.toString(), OrderOffer[].class);
//                    asksList = Arrays.asList(mapper.readValue(asksJsonArray.toString(),  OrderOffer[].class));
                    System.out.print(asksJsonArray.toJSONString());
                }

                JSONArray bidsJsonArray = (JSONArray) json.get("bids");
                List<Object> bidsList = new ArrayList<>();
                if(bidsJsonArray != null){
                    for(int i = 0; i < bidsJsonArray.size(); i++){
                        bidsList.add(bidsJsonArray.get(i));
                    }
                }

            }
            else if(type.equals("l2update")){
                Object changes = json.get("changes");

            }

        }
        catch(ParseException ex){
            ex.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: add logic to process each Json formatted message string
    }
}
