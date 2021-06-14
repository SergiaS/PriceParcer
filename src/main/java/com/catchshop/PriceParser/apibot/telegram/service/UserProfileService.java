package com.catchshop.PriceParser.apibot.telegram.service;

import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserProfileMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {
    private final UserProfileMongoRepository profileMongoRepository;

    //TODO: Add ScheduledExecutorService


    public UserProfileService(UserProfileMongoRepository profileMongoRepository) {
        this.profileMongoRepository = profileMongoRepository;
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
}
