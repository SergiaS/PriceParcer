package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MenuKeyboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class LanguageMessageHandler implements InputMessageHandler {
    private final LocaleMessageService localeMessageService;
    private final MenuKeyboardService menuKeyboardService;

    @Autowired
    public LanguageMessageHandler(LocaleMessageService localeMessageService, MenuKeyboardService menuKeyboardService) {
        this.localeMessageService = localeMessageService;
        this.menuKeyboardService = menuKeyboardService;
    }

    @Override
    public SendMessage handle(Message message) {
        String chatId = message.getChatId().toString();

        SendMessage replyToUser = menuKeyboardService.getMenuMessage(chatId, localeMessageService.getMessage("reply.menu.showLanguages"));
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_LANGUAGES;
    }

}
