package com.catchshop.PriceParser.bike.model;

import java.math.BigDecimal;

public class ShopOptions {
    private String comboField;
    private String color;
    private String size;
    private BigDecimal price;
    private String status;

    public ShopOptions(String comboField, BigDecimal price, String status) {
        this.comboField = comboField;
        this.price = price;
        this.status = status;
    }

    public ShopOptions(String color, String size, BigDecimal price, String status) {
        this.comboField = color + ", " + size;
        this.color = color;
        this.size = size;
        this.price = price;
        this.status = status;
    }

    public String getComboField() {
        return comboField;
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
