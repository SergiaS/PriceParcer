package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ErrorMessageHandler implements InputMessageHandler {
    private final LocaleMessageService localeMessageService;

    @Autowired
    public ErrorMessageHandler(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        String chatId = inputMessage.getChatId().toString();

        SendMessage replyToUser = new SendMessage();
        replyToUser.setText(localeMessageService.getMessage("reply.error.unknownCommand"));
        replyToUser.setChatId(chatId);
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_ERROR;
    }
}
