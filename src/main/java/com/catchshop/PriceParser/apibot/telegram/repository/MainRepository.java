package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.bike.model.Item;

import java.util.Set;

public interface MainRepository {

    BotStatus getBotStatus(Long userId);

    void setBotStatus(Long userId, BotStatus botStatus);

    UserProfile getUserProfile(Long userId);

    void saveUserProfile(Long userId, UserProfile userProfile);

    String getLocaleProfile(Long userId);

    void setLocaleProfile(Long userId, String locale);


    Set<Item> getAllItems(Long userId);

    Item getItem(Long userId, Item item);

    void saveItem(Long userId, Item item);

    void deleteItem(Long userId, Item item);
}
