package com.gsr.common;
import org.javatuples.Pair;

import java.util.List;

public class L2OrderResponse {
    public String type;
    public String product_id;
    public List<Pair<String, Pair<String, String>>> changes;
    public String time;

    public String getTime() { return time; }

    public String getType() { return type; }

    public String getProductId() { return product_id; }

    public List<Pair<String, Pair<String, String>>> getChanges() { return changes; }
}
