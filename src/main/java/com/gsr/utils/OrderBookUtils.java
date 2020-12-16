package com.gsr.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OrderBookUtils {
    public static final String COIN_BASE_WEBSOCKET = "wss://ws-feed.pro.coinbase.com";
    public static final String TYPE = "type";
    public static final String PRODUCT_IDS = "product_ids";
    public static final String CHANNELS = "channels";

    public enum Type{
        SUBSCRIBE("subscribe"),
        UNSUBSCRIBE("unsubscribe");

        private String type;
        Type(String type){
            this.type = type;
        }

        public String getType(){
            return type;
        }
    }

    public enum Channel{
        Ticker("ticker"),
        L2("level2");

        private String channel;
        Channel(String channel){
            this.channel = channel;
        }

        public String getChannel(){
            return channel;
        }
    }

    public enum Side{
        Buy("buy"),
        Sell("sell");

        private String side;
        Side(String side){
            this.side = side;
        }

        public String getSide(){
            return side;
        }
    }

    /**
     * Example subscribe message
     * {
     *     "type": "subscribe",
     *     "product_ids": [
     *         "BTC-USD"
     *     ],
     *     "channels": [
     *         "level2"
     *     ]
     * }
     *
     * @return
     */
    public static String constructJsonMessage(Type type, String productId, Channel channel){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TYPE, type.getType());

        // product ids json array
        JSONArray productIds = new JSONArray();
        productIds.add(productId);
        jsonObject.put(PRODUCT_IDS, productIds);

        // channel json array
        JSONArray channels = new JSONArray();
        channels.add(channel.getChannel());
        jsonObject.put(CHANNELS, channels);

        return jsonObject.toString();
    }
}