package com.catchshop.PriceParser.bike.model;

import com.catchshop.PriceParser.bike.enums.ParsedShop;

import java.util.List;
import java.util.Objects;

public class Item {
    private final String title;
    private final ParsedShop shopName;
    private final String URL;
    private final List<ItemOptions> itemOptionsList;
    private final String rangePrice;

    public Item(String title, ParsedShop shopName, String URL, List<ItemOptions> itemOptionsList, String rangePrice) {
        this.title = title;
        this.shopName = shopName;
        this.URL = URL;
        this.itemOptionsList = itemOptionsList;
        this.rangePrice = rangePrice;
    }

    public String getTitle() {
        return title;
    }

    public ParsedShop getShopName() {
        return shopName;
    }

    public String getURL() {
        return URL;
    }

    public List<ItemOptions> getItemOptionsList() {
        return itemOptionsList;
    }

    public String getRangePrice() {
        return rangePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item that = (Item) o;
        return Objects.equals(title, that.title) && shopName == that.shopName && Objects.equals(URL, that.URL) && Objects.equals(itemOptionsList, that.itemOptionsList) && Objects.equals(rangePrice, that.rangePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, shopName, URL, itemOptionsList, rangePrice);
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", shopName=" + shopName +
                ", URL='" + URL + '\'' +
                ", itemOptionsList=" + itemOptionsList +
                ", rangePrice='" + rangePrice + '\'' +
                '}';
    }
}
