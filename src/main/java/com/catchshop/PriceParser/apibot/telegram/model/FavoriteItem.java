package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FavoriteItem extends Item {

    private LocalDateTime dateTimeUpdate;

    public FavoriteItem(String TITLE, Shop SHOP, String URL, ItemOptions OPTIONS) {
        super(TITLE, SHOP, URL, OPTIONS);
        this.dateTimeUpdate = LocalDateTime.now().withNano(0);
    }

    public LocalDateTime getDateTimeUpdate() {
        return dateTimeUpdate;
    }

    public void setDateTimeUpdate(LocalDateTime dateTimeUpdate) {
        this.dateTimeUpdate = dateTimeUpdate;
    }

    public static List<FavoriteItem> fillDefaultFavorites() {
        List<FavoriteItem> list = new ArrayList<>();
        FavoriteItem favoriteItem1 = new FavoriteItem("GripGrab Classic Low Cut Sock",
                Shop.getExampleShop(ParsedShop.WIGGLE),
                "https://www.wiggle.co.uk/gripgrab-classic-low-cut-sock",
                new ItemOptions("Blue", "M", new BigDecimal("10.30"),"In stock"));
        FavoriteItem favoriteItem2 = new FavoriteItem("Under Armour HeatGear Armour Short Sleeve Compression Tee",
                Shop.getExampleShop(ParsedShop.WIGGLE),
                "https://www.wiggle.co.uk/under-armour-heatgear-armour-short-sleeve-compression-tee-1",
                new ItemOptions("Red", "Medium", new BigDecimal("20.58"),"In stock"));
        FavoriteItem favoriteItem3 = new FavoriteItem("ABUS AirBreaker Helmet - polar white",
                Shop.getExampleShop(ParsedShop.BIKE24),
                "https://www.bike24.com/p2322664.html",
                new ItemOptions("L (59-62cm)", new BigDecimal("150.83"),"Still 9 in stock"));

        list.add(favoriteItem1);
        list.add(favoriteItem2);
        list.add(favoriteItem3);
        return list;
    }

    public static FavoriteItem convertToFavoriteItem(ParseItem item, FavoriteItem target) {
        String group, color, size, status;
        BigDecimal price;
        ItemOptions options = null;
        for (ItemOptions itemOptions : item.getItemOptionsList()) {
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
}
