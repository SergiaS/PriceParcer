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
public class MenuMessageHandler implements InputMessageHandler {
    private final MenuKeyboardService menuKeyboardService;
    private final LocaleMessageService localeMessageService;

    @Autowired
    public MenuMessageHandler(MenuKeyboardService menuKeyboardService, LocaleMessageService localeMessageService) {
        this.menuKeyboardService = menuKeyboardService;
        this.localeMessageService = localeMessageService;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        String chatId = inputMessage.getChatId().toString();

        SendMessage replyToUser;
        replyToUser = menuKeyboardService.getMenuMessage(chatId, localeMessageService.getMessage("reply.menu.showMenu"));
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_MENU;
    }

}
