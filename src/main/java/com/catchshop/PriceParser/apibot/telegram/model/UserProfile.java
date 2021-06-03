package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;

import java.util.HashSet;
import java.util.Set;

public class UserProfile {

    private ParseItem tmpParsedParseItem;
    private Set<FavoriteItem> favorites;
    private BotStatus botStatus;
    private String languageTag;
    // and more shop settings ...

    public UserProfile() {
        this.botStatus = BotStatus.SHOW_MENU;
        this.favorites = new HashSet<>();
        this.languageTag = "en-EN";
    }

    public ParseItem getTmpParsedItem() {
        return tmpParsedParseItem;
    }

    public void setTmpParsedItem(ParseItem tmpParsedParseItem) {
        this.tmpParsedParseItem = tmpParsedParseItem;
    }

    public Set<FavoriteItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<FavoriteItem> favorites) {
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
