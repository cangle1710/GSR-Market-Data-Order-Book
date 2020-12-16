package com.gsr.common;

import com.gsr.utils.OrderBookUtils;

public class L2Changes {
    private OrderBookUtils.Side side;
    private double price;
    private double size;

    public OrderBookUtils.Side getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }
}
