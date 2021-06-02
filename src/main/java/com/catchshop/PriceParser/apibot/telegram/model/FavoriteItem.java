package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;

public class FavoriteItem extends Item {

    public FavoriteItem(String TITLE, Shop SHOP, String URL, ItemOptions OPTIONS) {
        super(TITLE, SHOP, URL, OPTIONS);
    }
}
