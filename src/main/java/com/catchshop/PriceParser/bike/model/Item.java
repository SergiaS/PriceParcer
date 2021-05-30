package com.catchshop.PriceParser.bike.model;

import java.util.List;
import java.util.Objects;

public class Item {
    private final String title;
    private final Shop shop;
    private final String URL;
    private ItemOptions tempItemOptions;
    private final List<ItemOptions> itemOptionsList;
    private final String rangePrice;

    public Item(String title, Shop shop, String URL, ItemOptions tempItemOptions, String rangePrice) {
        this.title = title;
        this.shop = shop;
        this.URL = URL;
        this.tempItemOptions = tempItemOptions;
        this.itemOptionsList = null;
        this.rangePrice = rangePrice;
    }

    public Item(String title, Shop shop, String URL, List<ItemOptions> itemOptionsList, String rangePrice) {
        this.title = title;
        this.shop = shop;
        this.URL = URL;
        this.tempItemOptions = null;
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

    public ItemOptions getTempItemOptions() {
        return tempItemOptions;
    }

    public void setTempItemOptions(ItemOptions tempItemOptions) {
        this.tempItemOptions = tempItemOptions;
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
        Item item = (Item) o;
        return Objects.equals(title, item.title) && Objects.equals(shop, item.shop) && Objects.equals(URL, item.URL) && Objects.equals(tempItemOptions, item.tempItemOptions) && Objects.equals(itemOptionsList, item.itemOptionsList) && Objects.equals(rangePrice, item.rangePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, shop, URL, tempItemOptions, itemOptionsList, rangePrice);
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
