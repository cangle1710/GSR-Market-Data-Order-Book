package com.gsr.marketdata;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        final ObjectMapper mapper = new ObjectMapper();

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
            }
            else if(type.equals(L2_UPDATE)){
                L2OrderResponse l2Response = mapper.readValue(json.toString(), L2OrderResponse.class);
            }
            System.out.println(message);
        }
        catch(ParseException ex){
            ex.printStackTrace();
         }  catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
