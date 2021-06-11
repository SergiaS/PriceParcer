package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserProfile {

    private ParseItem tmpParsedParseItem;
    private List<FavoriteItem> favorites;
    private BotStatus botStatus;
    private String languageTag;
    // and more shop settings ...

    @Autowired
    public UserProfile() {
        this.botStatus = BotStatus.SHOW_MENU;
        this.languageTag = "en-EN";
        this.favorites = new ArrayList<>();
        favorites.addAll(FavoriteItem.fillDefaultFavorites());
    }

    public ParseItem getTmpParsedItem() {
        return tmpParsedParseItem;
    }

    public void setTmpParsedItem(ParseItem tmpParsedParseItem) {
        this.tmpParsedParseItem = tmpParsedParseItem;
    }

    public List<FavoriteItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoriteItem> favorites) {
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
