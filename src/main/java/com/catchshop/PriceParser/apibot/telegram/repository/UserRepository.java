package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory DB.
 */
@Repository
public class UserRepository implements MainRepository {

    private final ResultManager resultManager;

    private final Map<Long, UserProfile> userProfilesDB = new HashMap<>();

    @Autowired
    public UserRepository(ResultManager resultManager) {
        this.resultManager = resultManager;
        userProfilesDB.put(564108458L, new UserProfile(564108458L, resultManager));
    }

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
            userProfile = new UserProfile(userId, resultManager);
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
    public ArrayDeque<FavoriteItem> getAllItems(Long userId) {
        UserProfile userProfile = userProfilesDB.get(userId);
        return userProfile.getFavorites();
    }

    @Override
    public ParseItem getItem(Long userId, ParseItem parseItem) {
        ArrayDeque<FavoriteItem> favorites = userProfilesDB.get(userId).getFavorites();
        if (favorites.contains(parseItem)) {
            return parseItem;
        }
        return null;
    }

    @Override
    public void saveItem(Long userId, FavoriteItem parseItem) {
        userProfilesDB.get(userId)
                .getFavorites().add(parseItem);
    }

    @Override
    public void deleteItem(Long userId, ParseItem parseItem) {
        UserProfile userProfile = userProfilesDB.get(userId);
        ArrayDeque<FavoriteItem> favorites = userProfile.getFavorites();
        favorites.remove(parseItem);
    }
}
