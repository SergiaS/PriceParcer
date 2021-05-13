package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.UserRequestProfile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory DB.
 */
@Repository
public class UserRepository implements MainRepository {

    private Map<Long, BotStatus> usersBotStatuses = new HashMap<>();
    private Map<Long, UserRequestProfile> usersRequestProfile = new HashMap<>();

    @Override
    public BotStatus getCurrentBotStatusFromUser(Long userId) {
        BotStatus botStatus = usersBotStatuses.get(userId);
        if (botStatus == null) {
            botStatus = BotStatus.MAIN_MENU;
        }
        return botStatus;
    }

    @Override
    public void setCurrentBotStatusToUser(Long userId, BotStatus botStatus) {
        usersBotStatuses.put(userId, botStatus);
    }

    @Override
    public UserRequestProfile getUserRequestProfile(Long userId) {
        UserRequestProfile userRequestProfile = usersRequestProfile.get(userId);
        if(userRequestProfile == null) {
            userRequestProfile = new UserRequestProfile();
        }
        return userRequestProfile;
    }

    @Override
    public void saveUserRequestProfile(Long userId, UserRequestProfile userRequestProfile) {
        usersRequestProfile.put(userId, userRequestProfile);
    }
}
