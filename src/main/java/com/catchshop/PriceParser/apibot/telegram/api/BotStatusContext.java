package com.catchshop.PriceParser.apibot.telegram.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStatusContext {

    private Map<BotStatus, InputMessageHandler> messageHandlers = new HashMap<>();

    @Autowired
    public BotStatusContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler ->
                this.messageHandlers.put(handler.getHandleName(), handler));
    }

    public SendMessage processInputMessage(BotStatus currentStatus, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentStatus);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotStatus currentStatus) {
        if (isMenuStatus(currentStatus)) {
            return messageHandlers.get(BotStatus.MAIN_MENU);
        } else if (isSearchStatus(currentStatus)) {
            return messageHandlers.get(BotStatus.SEARCH_MENU);
        }
        return messageHandlers.get(BotStatus.MAIN_MENU); // change in future
    }

    private boolean isMenuStatus(BotStatus currentStatus) {
        switch (currentStatus) {
            case MAIN_MENU:
            case SHOW_FAVORITE:
            case ADD_TO_FAVORITE:
            case LANGUAGE_SETTINGS:
                return true;
            default:
                return false;
        }
    }

    private boolean isSearchStatus(BotStatus currentStatus) {
        switch (currentStatus) {
            case SEARCH_MENU:
            case SEARCH_PROCESS:
                return true;
            default:
                return false;
        }
    }
}
