package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;

import java.util.List;
import java.util.Objects;

public class ParsedItem extends Item {
    private final List<ItemOptions> parsedOptionsList;
    private ItemOptions selectedOptions;
    private final String rangePrice;

    public ParsedItem(String title, Shop shop, String url, List<ItemOptions> parsedOptionsList, String rangePrice) {
        super(title, shop, url);
        this.parsedOptionsList = parsedOptionsList;
        this.selectedOptions = new ItemOptions();
        this.rangePrice = rangePrice;
    }

    public List<ItemOptions> getParsedOptionsList() {
        return parsedOptionsList;
    }

    public ItemOptions getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(ItemOptions selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public String getRangePrice() {
        return rangePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedItem parsedItem = (ParsedItem) o;
        return Objects.equals(parsedOptionsList, parsedItem.parsedOptionsList) && Objects.equals(rangePrice, parsedItem.rangePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parsedOptionsList, rangePrice);
    }

    @Override
    public String toString() {
        return "ParsedItem{" +
                "optionsList=" + parsedOptionsList +
                ", selectedOptions=" + selectedOptions +
                ", rangePrice='" + rangePrice + '\'' +
                '}';
    }
}
