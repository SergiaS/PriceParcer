package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.model.UserRequestProfile;

public interface MainRepository {

    BotStatus getCurrentBotStatusFromUser(Long userId);

    void setCurrentBotStatusToUser(Long userId, BotStatus botStatus);

    UserRequestProfile getUserRequestProfile(Long userId);

    void saveUserRequestProfile(Long userId, UserRequestProfile userRequestProfile);

}
