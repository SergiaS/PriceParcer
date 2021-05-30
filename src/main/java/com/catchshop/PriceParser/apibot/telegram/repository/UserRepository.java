package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.bike.model.Item;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * In-memory DB.
 */
@Repository
public class UserRepository implements MainRepository {

    private final Map<Long, UserProfile> userProfilesDB = new HashMap<>();

    @Override
    public BotStatus getBotStatus(Long userId) {
        UserProfile userProfile = getUserProfile(userId);
        return userProfile.getBotStatus();
    }

    @Override
    public void setBotStatus(Long userId, BotStatus botStatus) {
        UserProfile userProfile = getUserProfile(userId);
        userProfile.setBotStatus(botStatus);
        saveUserProfile(userId, userProfile);
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        UserProfile userProfile = userProfilesDB.get(userId);
        if (userProfile == null) {
            userProfile = new UserProfile();
        }
        return userProfile;
    }

    @Override
    public void saveUserProfile(Long userId, UserProfile userProfile) {
        userProfilesDB.put(userId, userProfile);
    }

    @Override
    public String getLocaleProfile(Long userId) {
        UserProfile userProfile = userProfilesDB.get(userId);
        if (userProfile == null) {
            return null;
        }
        return userProfile.getLanguageTag();
    }

    @Override
    public void setLocaleProfile(Long userId, String locale) {
        UserProfile userProfile = userProfilesDB.get(userId);
        userProfile.setLanguageTag(locale);
        saveUserProfile(userId, userProfile);
    }


    @Override
    public Set<Item> getAllItems(Long userId) {
        UserProfile userProfile = userProfilesDB.get(userId);
        return userProfile.getFavorites();
    }

    @Override
    public Item getItem(Long userId, Item item) {
        Set<Item> favorites = userProfilesDB.get(userId).getFavorites();
        if (favorites.contains(item)) {
            return item;
        }
        return null;
    }

    @Override
    public void saveItem(Long userId, Item item) {
        userProfilesDB.get(userId)
                .getFavorites().add(item);
    }

    @Override
    public void deleteItem(Long userId, Item item) {
        UserProfile userProfile = userProfilesDB.get(userId);
        Set<Item> favorites = userProfile.getFavorites();
        favorites.remove(item);
    }
}
