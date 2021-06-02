package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;

import java.util.Set;

public interface MainRepository {

    BotStatus getBotStatus(Long userId);

    void setBotStatus(Long userId, BotStatus botStatus);

    UserProfile getUserProfile(Long userId);

    void saveUserProfile(Long userId, UserProfile userProfile);

    String getLocaleProfile(Long userId);

    void setLocaleProfile(Long userId, String locale);


    Set<FavoriteItem> getAllItems(Long userId);

    ParseItem getItem(Long userId, ParseItem parseItem);

    void saveItem(Long userId, FavoriteItem parseItem);

    void deleteItem(Long userId, ParseItem parseItem);
}
