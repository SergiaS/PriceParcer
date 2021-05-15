package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MainMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuMessageHandler implements InputMessageHandler {
    private UserRepository userRepository;
    private MainMenuService mainMenuService;

    @Autowired
    public MenuMessageHandler(UserRepository userRepository, MainMenuService mainMenuService) {
        this.userRepository = userRepository;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_MENU;
    }

    private SendMessage processUserInput(Message inputMessage) {
        String chatId = inputMessage.getChatId().toString();

        SendMessage replyToUser;
        replyToUser = mainMenuService.getMainMenuMessage(chatId, "reply.menu.showMenu");
        return replyToUser;

//        SendMessage replyToUser = messageService.getReplyMessage(chatId, "button.menu.showMenu");
//        replyToUser.setReplyMarkup(getInlineMessageButtons(chatId));
//        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(String chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton buttonFavorite =
//                new InlineKeyboardButton(localeMessageService.getMessage(chatId, "button.menu.showFavorites"));
//        InlineKeyboardButton buttonSearch =
//                new InlineKeyboardButton(localeMessageService.getMessage(chatId, "button.menu.showSearch"));
//        InlineKeyboardButton buttonChangeLanguage =
//                new InlineKeyboardButton(localeMessageService.getMessage(chatId, "button.menu.showLanguages"));
//
//        buttonFavorite.setCallbackData("buttonFavorite");
//        buttonSearch.setCallbackData("buttonSearch");
//        buttonChangeLanguage.setCallbackData("buttonChangeLanguage");
//
//        List<InlineKeyboardButton> keyboardButtonList1 = new ArrayList<>();
//        keyboardButtonList1.add(buttonFavorite);
//
//        List<InlineKeyboardButton> keyboardButtonList2 = new ArrayList<>();
//        keyboardButtonList2.add(buttonSearch);
//
//        List<InlineKeyboardButton> keyboardButtonList3 = new ArrayList<>();
//        keyboardButtonList3.add(buttonChangeLanguage);
//
//        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//        rowList.add(keyboardButtonList1);
//        rowList.add(keyboardButtonList2);
//        rowList.add(keyboardButtonList3);
//
//        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}
