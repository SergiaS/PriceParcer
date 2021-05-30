package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.bike.model.Item;

import java.util.HashSet;
import java.util.Set;

public class UserProfile {

    private Item tmpParsedItem;
    private Set<Item> favorites;
    private BotStatus botStatus;
    private String languageTag;
    // and more shop settings ...

    public UserProfile() {
        this.botStatus = BotStatus.SHOW_MENU;
        this.favorites = new HashSet<>();
        this.languageTag = "en-EN";
    }

    public Item getTmpParsedItem() {
        return tmpParsedItem;
    }

    public void setTmpParsedItem(Item tmpParsedItem) {
        this.tmpParsedItem = tmpParsedItem;
    }

    public Set<Item> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<Item> favorites) {
        this.favorites = favorites;
    }

    public BotStatus getBotStatus() {
        return botStatus;
    }

    public void setBotStatus(BotStatus botStatus) {
        this.botStatus = botStatus;
    }

    public String getLanguageTag() {
        return this.languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }

}
