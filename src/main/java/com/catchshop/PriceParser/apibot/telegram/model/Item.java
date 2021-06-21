package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.model.Shop;
import org.springframework.data.annotation.PersistenceConstructor;

public class Item {
    private String title;
    private Shop shop;
    private String url;

    @PersistenceConstructor
    public Item() {
    }

    public Item(String title, Shop shop, String url) {
        this.title = title;
        this.shop = shop;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public Shop getShop() {
        return shop;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", shop=" + shop +
                ", url='" + url + '\'' +
                '}';
    }
}
