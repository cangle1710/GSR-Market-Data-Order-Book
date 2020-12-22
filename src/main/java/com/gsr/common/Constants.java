package com.gsr.common;

public class Constants {
    public static final String COIN_BASE_WEBSOCKET = "wss://ws-feed.pro.coinbase.com";
    public static final String TYPE = "type";
    public static final String PRODUCT_IDS = "product_ids";
    public static final String CHANNELS = "channels";
    public static final String SNAPSHOT = "snapshot";
    public static final String L2_UPDATE = "l2update";
    public static final String BUY = "buy";
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
}
