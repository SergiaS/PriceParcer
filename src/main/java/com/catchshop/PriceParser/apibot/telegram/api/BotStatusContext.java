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

    private final Map<BotStatus, InputMessageHandler> messageHandlers = new HashMap<>();

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
        if (currentStatus.equals(BotStatus.SHOW_SEARCH)) {
            return messageHandlers.get(BotStatus.SHOW_SEARCH);
        } else if (isParseActions(currentStatus)) {
            return messageHandlers.get(BotStatus.SHOW_PARSE);
        } else if (isFavoritesAction(currentStatus)) {
            return messageHandlers.get(BotStatus.SHOW_FAVORITES);
        } else if (currentStatus.equals(BotStatus.SHOW_LANGUAGES)) {
            return messageHandlers.get(BotStatus.SHOW_LANGUAGES);
        } else if (currentStatus.equals(BotStatus.SHOW_ERROR)) {
            return messageHandlers.get(BotStatus.SHOW_ERROR);
        }
        return messageHandlers.get(BotStatus.SHOW_MENU);
    }

    private boolean isParseActions(BotStatus currentState) {
        switch (currentState) {
            case SHOW_PARSE:
            case FILLING_ITEM:
            case ASK_TRACKING:
                return true;
            default:
                return false;
        }
    }

    private boolean isFavoritesAction(BotStatus currentState) {
        switch (currentState) {
            case SHOW_FAVORITES:
            case SHOW_FAVORITES_DELETE:
                return true;
            default:
                return false;
        }
    }
}
