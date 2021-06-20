package com.catchshop.PriceParser.bike.model;

import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemOptions {
    private String group; // comboField/mixed
    private String color;
    private String size;
    private BigDecimal price;
    private String status;

    @PersistenceConstructor
    public ItemOptions() {
    }

    public ItemOptions(BigDecimal price, String status) {
        this.price = price;
        this.status = status;
    }

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

    public ItemOptions(String group, String color, String size, BigDecimal price, String status) {
        this.group = group;
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

    public void setGroup(String group) {
        this.group = group;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOptions that = (ItemOptions) o;
        return Objects.equals(group, that.group) && Objects.equals(color, that.color) && Objects.equals(size, that.size) && Objects.equals(price, that.price) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, color, size, price, status);
    }

    @Override
    public String toString() {
        return "ItemOptions{" +
                "group='" + group + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}

