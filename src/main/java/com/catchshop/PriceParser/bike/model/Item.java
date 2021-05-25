package com.catchshop.PriceParser.bike.model;

import java.util.List;
import java.util.Objects;

public class Item {
    private final String title;
    private final Shop shop;
    private final String URL;
    private final List<ItemOptions> itemOptionsList;
    private final String rangePrice;

    public Item(String title, Shop shop, String URL, List<ItemOptions> itemOptionsList, String rangePrice) {
        this.title = title;
        this.shop = shop;
        this.URL = URL;
        this.itemOptionsList = itemOptionsList;
        this.rangePrice = rangePrice;
    }

    public String getTitle() {
        return title;
    }

    public Shop getShop() {
        return shop;
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
        return Objects.equals(title, that.title) && shop == that.shop && Objects.equals(URL, that.URL) && Objects.equals(itemOptionsList, that.itemOptionsList) && Objects.equals(rangePrice, that.rangePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, shop, URL, itemOptionsList, rangePrice);
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", shop=" + shop +
                ", URL='" + URL + '\'' +
                ", itemOptionsList=" + itemOptionsList +
                ", rangePrice='" + rangePrice + '\'' +
                '}';
    }
}
