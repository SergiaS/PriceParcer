package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import com.catchshop.PriceParser.bike.shops.MainParser;
import com.catchshop.PriceParser.bike.util.ShopHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * In-memory DB.
 */
@Repository
public class UserRepository implements MainRepository {

    private final ResultManager resultManager;
    private final int updateTime;

    private final Map<Long, UserProfile> userProfilesDB = new HashMap<>();

    @Autowired
    public UserRepository(ResultManager resultManager) {
        this.resultManager = resultManager;
        this.updateTime = 15;
        userProfilesDB.put(564108458L, new UserProfile());
        userProfilesDB.forEach((k,v) -> startTrackingUserFavorites(k));
    }

    private void startTrackingUserFavorites(Long userId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        startScheduler(userId).apply(executorService);
    }

    private Function<ScheduledExecutorService, Void> startScheduler(Long userId) {
        return (executorService -> {
            executorService.scheduleWithFixedDelay(() -> {
                List<FavoriteItem> allItems = getAllItems(userId);
                if (allItems.size() == 0) {
                    System.out.println("list of user favorites is empty - shutdown!");
                    executorService.shutdown();
                }

                for (int i = 0; i < allItems.size(); i++) {
                    FavoriteItem oldData = allItems.get(i);
                    MainParser shopParser = ShopHelper.storeIdentifier(oldData.getUrl());
                    ParseItem newItem = shopParser.parseItemInfo(oldData.getUrl());
                    FavoriteItem newData = FavoriteItem.convertToFavoriteItem(newItem, oldData);

                    resultManager.notifyIfItemUpdated(userId.toString(), oldData, newData);
                    allItems.set(i, newData);
                }
            }, 0, updateTime, TimeUnit.SECONDS);
            return null;
        });
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
    public List<FavoriteItem> getAllItems(Long userId) {
        UserProfile userProfile = userProfilesDB.get(userId);
        return userProfile.getFavorites();
    }

    @Override
    public ParseItem getItem(Long userId, ParseItem parseItem) {
        List<FavoriteItem> favorites = userProfilesDB.get(userId).getFavorites();
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
        List<FavoriteItem> favorites = userProfile.getFavorites();
        favorites.remove(parseItem);
    }
}
