package com.gsr.common;

import org.javatuples.Pair;
import java.util.List;

public class SnapShotOrderResponse {
    public String type;
    public String product_id;
    public List<Pair<String, String>> asks;
    public List<Pair<String, String>> bids;

    public String getType() {
        return type;
    }

    public String getProductId() {
        return product_id;
    }

    public List<Pair<String, String>> getAsks() {
        return asks;
    }

    public List<Pair<String, String>> getBids() {
        return bids;
    }
}
