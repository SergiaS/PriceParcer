package com.catchshop.PriceParser.bike.model;

import com.catchshop.PriceParser.bike.enums.ParsedShop;

import java.util.List;
import java.util.Objects;

public class FavoriteItem {
    private String itemName;
    private ParsedShop shopName;
    private String URL;
    private List<ShopOptions> shopOptionsList;
    private String rangePrice;

    public FavoriteItem(String itemName, ParsedShop shopName, String URL, List<ShopOptions> shopOptionsList, String rangePrice) {
        this.itemName = itemName;
        this.shopName = shopName;
        this.URL = URL;
        this.shopOptionsList = shopOptionsList;
        this.rangePrice = rangePrice;
    }

    public String getItemName() {
        return itemName;
    }

    public ParsedShop getShopName() {
        return shopName;
    }

    public String getURL() {
        return URL;
    }

    public List<ShopOptions> getShopOptionsList() {
        return shopOptionsList;
    }

    public String getRangePrice() {
        return rangePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteItem that = (FavoriteItem) o;
        return Objects.equals(itemName, that.itemName) && shopName == that.shopName && Objects.equals(URL, that.URL) && Objects.equals(shopOptionsList, that.shopOptionsList) && Objects.equals(rangePrice, that.rangePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, shopName, URL, shopOptionsList, rangePrice);
    }

    @Override
    public String toString() {
        return "FavoriteItem{" +
                "itemName='" + itemName + '\'' +
                ", shopName=" + shopName +
                ", URL='" + URL + '\'' +
                ", shopOptionsList=" + shopOptionsList +
                ", rangePrice='" + rangePrice + '\'' +
                '}';
    }
}
