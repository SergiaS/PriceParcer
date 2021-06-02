package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;

import java.util.List;
import java.util.Objects;

public class ParseItem extends Item {
    private final List<ItemOptions> itemOptionsList;
    private final String rangePrice;

    public ParseItem(String TITLE, Shop SHOP, String URL, ItemOptions OPTIONS, String rangePrice) {
        super(TITLE, SHOP, URL, OPTIONS);
        this.itemOptionsList = null;
        this.rangePrice = rangePrice;
    }

    public ParseItem(String TITLE, Shop SHOP, String URL, List<ItemOptions> itemOptionsList, String rangePrice) {
        super(TITLE, SHOP, URL, null);
        this.itemOptionsList = itemOptionsList;
        this.rangePrice = rangePrice;
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
        ParseItem parseItem = (ParseItem) o;
        return Objects.equals(itemOptionsList, parseItem.itemOptionsList) && Objects.equals(rangePrice, parseItem.rangePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemOptionsList, rangePrice);
    }

    @Override
    public String toString() {
        return "ParseItem{" +
                "itemOptionsList=" + itemOptionsList +
                ", rangePrice='" + rangePrice + '\'' +
                '}';
    }
}
