package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.List;
import java.util.Objects;

public class ParsedItem extends Item {
    private List<ItemOptions> parsedOptionsList;
    private ItemOptions selectedOptions;
    private String rangePrice;

    @PersistenceConstructor
    public ParsedItem() {

    }

    public ParsedItem(String title, Shop shop, String url, List<ItemOptions> parsedOptionsList, String rangePrice) {
        super(title, shop, url);
        this.parsedOptionsList = parsedOptionsList;
        this.selectedOptions = new ItemOptions();
        this.rangePrice = rangePrice;
    }

    public List<ItemOptions> getParsedOptionsList() {
        return parsedOptionsList;
    }

    public void setParsedOptionsList(List<ItemOptions> parsedOptionsList) {
        this.parsedOptionsList = parsedOptionsList;
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

    public void setRangePrice(String rangePrice) {
        this.rangePrice = rangePrice;
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
