package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;

public interface MainRepository {

    BotStatus getBotStatus(Long userId);

    void setBotStatus(Long userId, BotStatus botStatus);

    UserProfile getUserProfile(Long userId);

    void saveUserProfile(Long userId, UserProfile userProfile);

    String getLocaleProfile(Long userId);

    void setLocaleProfile(Long userId, String locale);
}
