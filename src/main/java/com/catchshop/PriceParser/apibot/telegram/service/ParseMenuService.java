package com.catchshop.PriceParser.apibot.telegram.service;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
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
public class ParseMenuService {

    private final LocaleMessageService localeMessageService;
    private final PriceParserTelegramBot telegramBot;
    private final UserRepository userRepository;

    @Autowired
    public ParseMenuService(LocaleMessageService localeMessageService, @Lazy PriceParserTelegramBot telegramBot, UserRepository userRepository) {
        this.localeMessageService = localeMessageService;
        this.telegramBot = telegramBot;
        this.userRepository = userRepository;
    }

    public SendMessage getParseMenu(String chatId, String userMsg) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getParseMenuKeyboard(chatId);
        return createMessageWithKeyboard(chatId, userMsg, replyKeyboardMarkup);
    }

    public ReplyKeyboardMarkup getParseMenuKeyboard(String chatId) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        if (userRepository.getBotStatus(Long.valueOf(chatId)).equals(BotStatus.SHOW_PARSE_END)){
            keyboard.add(addKeyboardRowByMessage("button.menu.showParse"));
            keyboard.add(addKeyboardRowByMessage("button.menu.showFavorites"));
        }
        keyboard.add(addKeyboardRowByMessage("button.menu.showMenu"));

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private KeyboardRow addKeyboardRowByMessage(String msg) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(localeMessageService.getMessage(msg)));
        return row;
    }

    private SendMessage createMessageWithKeyboard(String chatId, String userMsg, ReplyKeyboardMarkup replyKeyboardMarkup) {
        final  SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(userMsg);

        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}
