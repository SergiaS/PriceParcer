package com.catchshop.PriceParser.apibot.telegram.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {

    SendMessage handle(Message message);

    BotStatus getHandleName();

}
