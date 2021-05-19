package com.catchshop.PriceParser.apibot.telegram.service;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchMenuService {

    private final LocaleMessageService localeMessageService;
    private final PriceParserTelegramBot telegramBot;

    @Autowired
    public SearchMenuService(LocaleMessageService localeMessageService, @Lazy PriceParserTelegramBot telegramBot) {
        this.localeMessageService = localeMessageService;
        this.telegramBot = telegramBot;
    }

    public SendMessage getSearchMenuMessage(String chatId, String userMsg) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getSearchMenuKeyboard();
        return createMessageWithKeyboard(chatId, userMsg, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getSearchMenuKeyboard() {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(localeMessageService.getMessage("button.menu.showMenu")));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(final String chatId, String textMessage, final ReplyKeyboardMarkup replyKeyboardMarkup) {
//        telegramBot.sendMessage(new SendMessage(chatId, "bla3"));

        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);

        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}
