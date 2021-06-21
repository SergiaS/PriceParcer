package com.catchshop.PriceParser.apibot.telegram.service;

import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserProfileMongoRepository;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import com.catchshop.PriceParser.bike.shops.MainParser;
import com.catchshop.PriceParser.bike.util.ShopHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserProfileService {
    private final UserProfileMongoRepository profileMongoRepository;
    private final ResultManager resultManager;

    public UserProfileService(UserProfileMongoRepository profileMongoRepository, ResultManager resultManager) {
        this.profileMongoRepository = profileMongoRepository;
        this.resultManager = resultManager;

        List<UserProfile> profiles = getAllProfiles();
        for (UserProfile profile : profiles) {
            startTrackingUserFavorites(profile.getChatId());
        }
    }

    public List<UserProfile> getAllProfiles() {
        return profileMongoRepository.findAll();
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return profileMongoRepository.save(userProfile);
    }

    public UserProfile getUserProfileData(long chatId) {
        return profileMongoRepository.findByChatId(chatId);
    }

    public void deleteUsersProfile(String userProfileId) {
        profileMongoRepository.deleteById(userProfileId);
    }

    public void deleteAllUsersProfile() {
        profileMongoRepository.deleteAll();
    }

    public void startTrackingUserFavorites(Long chatId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        startScheduler(chatId).apply(executorService);
    }

    private Function<ScheduledExecutorService, Void> startScheduler(Long chatId) {
        return (executorService -> {
            executorService.scheduleWithFixedDelay(() -> {
                UserProfile userProfileData = getUserProfileData(chatId);
                List<FavoriteItem> allItems = userProfileData.getFavorites();
                if (allItems.size() == 0) {
                    log.info("List of user favorites chatId={} is empty - shutdown!", chatId);
                    executorService.shutdown();
                }

                boolean gotUpdate = false;
                for (int i = 0; i < allItems.size(); i++) {
                    FavoriteItem oldData = allItems.get(i);
                    MainParser shopParser = ShopHelper.storeIdentifier(oldData.getUrl());
                    ParsedItem newItem = shopParser.parseItemInfo(oldData.getUrl());
                    FavoriteItem newData = FavoriteItem.convertToFavoriteItem(newItem, oldData);
                    log.info("Checking for updates: chatId={}, Favorite={}", chatId, oldData.getTitle());

                    boolean isUpdated = resultManager.notifyIfItemUpdated(chatId.toString(), oldData, newData);
                    if (isUpdated) {
                        gotUpdate = true;
                    }
                    allItems.set(i, newData);
                }
                if (gotUpdate) {
                    saveUserProfile(userProfileData);
                    log.info("Saving changes to DB...");
                }
            }, 0, 15L, TimeUnit.MINUTES);
            return null;
        });
    }
}
