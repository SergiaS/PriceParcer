package com.catchshop.PriceParser.apibot.telegram.api;

import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LanguageMenuService;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MainMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class TelegramStart {
    private BotStatusContext botStatusContext;
    private UserRepository userRepository;
    private LocaleMessageService localeMessageService;
    private MainMenuService mainMenuService;
    private LanguageMenuService languageMenuService;

    @Autowired
    public TelegramStart(BotStatusContext botStatusContext, UserRepository userRepository, LocaleMessageService localeMessageService, MainMenuService mainMenuService, LanguageMenuService languageMenuService) {
        this.botStatusContext = botStatusContext;
        this.userRepository = userRepository;
        this.localeMessageService = localeMessageService;
        this.mainMenuService = mainMenuService;
        this.languageMenuService = languageMenuService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    callbackQuery.getFrom().getUserName(), callbackQuery.getFrom().getId(), callbackQuery.getData());
            return processCallbackQuery(callbackQuery);
        }

        SendMessage replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User: {}, chatId: {}, with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    // обработка при нажатии кнопки
    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final String chatId = buttonQuery.getMessage().getChatId().toString();
        final Long userId = buttonQuery.getFrom().getId();

        return new SendMessage(chatId, "processCallbackQuery");

//        BotStatus botStatus = null;
//        BotApiMethod<?> callbackAnswer = mainMenuService.getMainMenuMessage(chatId, messageService.getMessage("button.menu.showMenu"));
//        String buttonQueryData = buttonQuery.getData();
//        if (buttonQueryData.equals("button.menu.showSearch")) {
////            callbackAnswer = new SendMessage(chatId, "reply.search.searchMenu");
//            callbackAnswer = sendAnswerCallbackQuery("reply.menu.showSearch", false, buttonQuery);
//            botStatus = BotStatus.SHOW_SEARCH;
//        } else if (buttonQueryData.equals("button.menu.showFavorite")) {
////            callbackAnswer = new SendMessage(chatId, "reply.menu.favorite");
//            callbackAnswer = sendAnswerCallbackQuery("reply.menu.showFavorite", true, buttonQuery);
//            botStatus = BotStatus.SHOW_FAVORITE;
//        } else if (buttonQueryData.equals("button.menu.changeLanguage")) {
////            callbackAnswer = new SendMessage(chatId, "reply.menu.changeLanguage");
//            callbackAnswer = languageMenuService.getLanguageMenuMessage(chatId, "button.menu.changeLanguage");
//            botStatus = BotStatus.CHANGE_LANGUAGE;
//        }
//        userRepository.setCurrentBotStatusToUser(userId, botStatus);
//        return callbackAnswer;
    }

    // обработка при отправке сообщения
    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long userId = message.getFrom().getId();
        BotStatus botStatus = null;

        if (userRepository.getBotStatus(userId) == null || inputMessage.equals(localeMessageService.getMessage("button.menu.showMenu"))) {
            botStatus = BotStatus.SHOW_MENU;
        } else if (userRepository.getBotStatus(userId) == BotStatus.SHOW_SEARCH || inputMessage.equals(localeMessageService.getMessage("button.menu.showSearch"))) {
            botStatus = BotStatus.SHOW_SEARCH;
        } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.showFavorites"))) {
            botStatus = BotStatus.SHOW_FAVORITE;
        } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.showLanguages"))) {
            botStatus = BotStatus.SHOW_LANGUAGES;
        } // changing locale
        else if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.english")) ||
                inputMessage.equals(localeMessageService.getMessage("button.menu.language.ukrainian")) ||
                inputMessage.equals(localeMessageService.getMessage("button.menu.language.russian"))) {

            if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.english"))) {
                userRepository.setLocaleProfile(userId, "en-EN");
            } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.ukrainian"))) {
                userRepository.setLocaleProfile(userId, "ua-UA");
            } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.russian"))) {
                userRepository.setLocaleProfile(userId, "ru-RU");
            }
            botStatus = BotStatus.SHOW_MENU;
            localeMessageService.setLanguageTag(userRepository.getLocaleProfile(userId));
        } // error zone
        else {
            botStatus = BotStatus.SHOW_ERROR;
            System.out.println("BOOM in TelegramStart");
        }
        userRepository.setBotStatus(userId, botStatus);
        return botStatusContext.processInputMessage(botStatus, message);
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        return answerCallbackQuery;
    }
}
