package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "userProfile")
public class UserProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private long chatId;
    private List<FavoriteItem> favorites;
    private BotStatus botStatus;
    private String languageTag;
    // and more shop settings ...

    public UserProfile(Long chatId) {
        this.chatId = chatId;
        this.botStatus = BotStatus.SHOW_MENU;
        this.languageTag = "en-EN";
        this.favorites = new ArrayList<>();
//        System.out.println(" = = = UserProfile = = =");
//        favorites.addAll(FavoriteItem.fillDefaultFavorites());
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
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
