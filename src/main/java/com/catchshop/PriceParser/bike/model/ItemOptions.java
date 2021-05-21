package com.catchshop.PriceParser.bike.model;

import java.math.BigDecimal;

public class ItemOptions {
    private String group; // comboField
    private String color;
    private String size;
    private BigDecimal price;
    private String status;

    public ItemOptions(String group, BigDecimal price, String status) {
        this.group = group;
        this.price = price;
        this.status = status;
    }

    public ItemOptions(String color, String size, BigDecimal price, String status) {
        this.color = color;
        this.size = size;
        this.price = price;
        this.status = status;
    }

    public String getGroup() {
        return group;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}
