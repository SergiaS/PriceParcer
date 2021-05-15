package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory DB.
 */
@Repository
public class UserRepository implements MainRepository {

    private Map<Long, UserProfile> users = new HashMap<>();

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
        UserProfile userProfile = users.get(userId);
        if(userProfile == null) {
            userProfile = new UserProfile();
        }
        return userProfile;
    }

    @Override
    public void saveUserProfile(Long userId, UserProfile userProfile) {
        users.put(userId, userProfile);
    }

    @Override
    public String getLocaleProfile(Long userId) {
        UserProfile userProfile = users.get(userId);
        if (userProfile == null) {
            return null;
        }
        return userProfile.getLanguageTag();
    }

    @Override
    public void setLocaleProfile(Long userId, String locale) {
        UserProfile userProfile = users.get(userId);
        userProfile.setLanguageTag(locale);
        saveUserProfile(userId, userProfile);
    }
}
