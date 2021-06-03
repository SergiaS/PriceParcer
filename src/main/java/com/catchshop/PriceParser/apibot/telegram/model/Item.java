package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;

public abstract class Item {
    private final String title;
    private final Shop shop;
    private final String url;
    private ItemOptions options;

    public Item(String title, Shop shop, String url, ItemOptions options) {
        this.title = title;
        this.shop = shop;
        this.url = url;
        this.options = options;
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

    public ItemOptions getOptions() {
        return options;
    }

    public void setOptions(ItemOptions options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", shop=" + shop +
                ", url='" + url + '\'' +
                ", options=" + options +
                '}';
    }
}
