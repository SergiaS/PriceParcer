package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FavoriteItem extends Item {

    private ItemOptions options;

    public FavoriteItem(String title, Shop shop, String url, ItemOptions options) {
        super(title, shop, url);
        this.options = options;
//        System.out.println(" @ FavoriteItem @");
    }

    public ItemOptions getOptions() {
        return options;
    }

    public void setOptions(ItemOptions options) {
        this.options = options;
    }

    public static List<FavoriteItem> fillDefaultFavorites() {
        List<FavoriteItem> list = new ArrayList<>();
        FavoriteItem favoriteItem1 = new FavoriteItem("Endura FS260 Pro Bib Shorts",
                Shop.getExampleShop(ParsedShop.WIGGLE),
                "https://www.wiggle.co.uk/endura-fs260-pro-bib-shorts-1",
                new ItemOptions("Black", "Large", new BigDecimal("105.82"),"In stock"));
        FavoriteItem favoriteItem2 = new FavoriteItem("Under Armour HeatGear Armour Short Sleeve Compression Tee",
                Shop.getExampleShop(ParsedShop.WIGGLE),
                "https://www.wiggle.co.uk/under-armour-heatgear-armour-short-sleeve-compression-tee-1",
                new ItemOptions("Red", "Medium", new BigDecimal("20.58"),"In stock"));
        FavoriteItem favoriteItem3 = new FavoriteItem("ABUS AirBreaker Helmet - polar white",
                Shop.getExampleShop(ParsedShop.BIKE24),
                "https://www.bike24.com/p2322664.html",
                new ItemOptions("L (59-62cm)", new BigDecimal("150.83"),"Still 9 in stock"));

        list.add(favoriteItem1);
        list.add(favoriteItem3);
        list.add(favoriteItem2);
        return list;
    }

    /** Simple converter from ParseItem to FavoriteItem
     * when needs to save parsedItem into favorites - use it
     */
    public static FavoriteItem convertToFavoriteItem(ParsedItem parsedItem) {
        String title = parsedItem.getTitle();
        String url = parsedItem.getUrl();
        Shop shop = parsedItem.getShop();

        String group = parsedItem.getSelectedOptions().getGroup();
        String color = parsedItem.getSelectedOptions().getColor();
        String size = parsedItem.getSelectedOptions().getSize();
        String status = parsedItem.getSelectedOptions().getStatus();
        BigDecimal price = parsedItem.getSelectedOptions().getPrice();

        return new FavoriteItem(title, shop, url, new ItemOptions(group, color, size, price, status));
    }

    /** Converts ParsedItem (SchedulerExecutionService - update object) to FavoriteItem for additional comparing
     */
    public static FavoriteItem convertToFavoriteItem(ParsedItem item, FavoriteItem target) {
        String group, color, size, status;
        BigDecimal price;
        ItemOptions options = null;
        for (ItemOptions itemOptions : item.getParsedOptionsList()) {
            if (itemOptions.getColor() != null && itemOptions.getColor().equals(target.getOptions().getColor()) &&
                    itemOptions.getSize() != null && itemOptions.getSize().equals(target.getOptions().getSize())) {
                color = itemOptions.getColor();
                size = itemOptions.getSize();
                price = itemOptions.getPrice();
                status = itemOptions.getStatus();
                options = new ItemOptions(color, size, price, status);
            } else if (itemOptions.getGroup() != null && itemOptions.getGroup().equals(target.getOptions().getGroup())) {
                group = itemOptions.getGroup();
                price = itemOptions.getPrice();
                status = itemOptions.getStatus();
                options = new ItemOptions(group, price, status);
            }
        }
        return new FavoriteItem(target.getTitle(), target.getShop(), target.getUrl(), options);
    }

    @Override
    public String toString() {
        return "FavoriteItem{" +
                "title='" + getTitle() +
                ", shop=" + getShop() +
                ", url='" + getUrl() +
                '}';
    }
}
