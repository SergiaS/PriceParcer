package com.catchshop.PriceParser.bike.shops.wiggle.model;

import com.catchshop.PriceParser.bike.shops.wiggle.WiggleParser;

import java.math.BigDecimal;

public class PriceOptions {
    private String size; // похорошему ENUM
    private String color; // похорошему ENUM
    private BigDecimal price; // похорошему ENUM
    private String status;

    public PriceOptions(String size, String color, BigDecimal price, String status) {
        this.size = size;
        this.color = color;
        this.price = price;
        this.status = status;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "PriceOptions{" +
//                "size='" + size + '\'' +
                (size == null || size.isEmpty() ? "" : ", size='" + size + '\'') +
                (color == null || color.isEmpty() ? "" : ", color='" + color + '\'') +
                ", price=" + WiggleParser.CURRENCY_SIGN + price +
                ", status=" + status +
                '}';
    }
}
