package com.catchshop.PriceParser.apibot.telegram.service;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuKeyboardService {
    private final LocaleMessageService localeMessageService;
    private final UserRepository userRepository;

    @Autowired
    public MenuKeyboardService(LocaleMessageService localeMessageService, UserRepository userRepository) {
        this.localeMessageService = localeMessageService;
        this.userRepository = userRepository;
    }

    public SendMessage getMenuMessage(String chatId, String userMsg) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getMenuKeyboard(Long.valueOf(chatId));
        return createMessageWithKeyboard(chatId, userMsg, replyKeyboardMarkup);
    }

    public ReplyKeyboardMarkup getMenuKeyboard(Long chatId) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        BotStatus botStatus = userRepository.getBotStatus(chatId);

        if (botStatus.equals(BotStatus.SHOW_MENU)) {
            // search and parse buttons in one row
            List<String> buttons = new ArrayList<>();
            buttons.add("button.menu.showSearch");
            if (chatId == 564108458) {
                buttons.add("button.menu.showParse");
            }
            keyboard.add(keyboardRowsConstructor(buttons.toArray(new String[0])));

            // favorites button
            keyboard.add(keyboardRowsConstructor("button.menu.showFavorites"));

            // languages button
            keyboard.add(keyboardRowsConstructor("button.menu.showLanguages"));
        } else if (botStatus.equals(BotStatus.SHOW_LANGUAGES)) {
            KeyboardRow row = keyboardRowsConstructor(
                    "button.menu.language.ukrainian",
                    "button.menu.language.english",
                    "button.menu.language.russian");
            keyboard.add(row);
        } else if (botStatus.equals(BotStatus.SHOW_PARSE_END)) {
            KeyboardRow row = keyboardRowsConstructor(
                    "button.menu.showParse",
                    "button.menu.showFavorites");
            keyboard.add(row);
        }

        if (!botStatus.equals(BotStatus.SHOW_MENU)) {
            keyboard.add(keyboardRowsConstructor("button.menu.showMenu"));
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    protected KeyboardRow keyboardRowsConstructor(String... menuButtons) {
        KeyboardRow row = new KeyboardRow();
        for (String menuButton : menuButtons) {
            row.add(new KeyboardButton(localeMessageService.getMessage(menuButton)));
        }
        return row;
    }

    protected SendMessage createMessageWithKeyboard(String chatId, String userMsg, ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(userMsg);

        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}
