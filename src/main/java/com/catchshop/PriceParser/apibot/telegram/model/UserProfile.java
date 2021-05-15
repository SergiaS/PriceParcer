package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;

import java.util.List;

public class UserProfile {

    private String currentRequest;
    private List<String> favoriteRequests;
    private BotStatus botStatus;
    private String languageTag;

    public UserProfile() {
        this.botStatus = BotStatus.SHOW_MENU;
        this.languageTag = "en-EN";
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
