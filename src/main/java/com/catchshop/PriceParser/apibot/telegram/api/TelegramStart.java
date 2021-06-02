package com.catchshop.PriceParser.apibot.telegram.api;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.bike.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final BotStatusContext botStatusContext;
    private final UserRepository userRepository;
    private final LocaleMessageService localeMessageService;
    private final PriceParserTelegramBot telegramBot;

    @Autowired
    public TelegramStart(BotStatusContext botStatusContext, UserRepository userRepository, LocaleMessageService localeMessageService, @Lazy PriceParserTelegramBot telegramBot) {
        this.botStatusContext = botStatusContext;
        this.userRepository = userRepository;
        this.localeMessageService = localeMessageService;
        this.telegramBot = telegramBot;
    }

    public BotApiMethod<?> handleUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    callbackQuery.getFrom().getUserName(), callbackQuery.getFrom().getId(), callbackQuery.getData());
            processCallbackQueryFillingFavoriteItem(update);
        }

        BotApiMethod<?> replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User: {}, chatId: {}, with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    /**
     * Button click (callback) handler
     * @param update
     * For now it just change Update object without return.
     */
    private void processCallbackQueryFillingFavoriteItem(Update update) {
        CallbackQuery buttonQuery = update.getCallbackQuery();
//        final String userChoice = buttonQuery.getMessage().getText();
//        final Long chatId = buttonQuery.getMessage().getChatId();
        final Long userId = buttonQuery.getFrom().getId();

        UserProfile userProfile = userRepository.getUserProfile(userId);

//        BotApiMethod<?> callbackAnswer = null;
        String buttonQueryData = buttonQuery.getData();

        BotStatus botStatus = userProfile.getBotStatus();
        Item tmpParsedItem = userProfile.getTmpParsedItem();

        Message message = buttonQuery.getMessage();
        message.setFrom(buttonQuery.getFrom());
        message.setText(buttonQueryData);
        if (botStatus.equals(BotStatus.ASK_COLOR)) {
            tmpParsedItem.getTempItemOptions().setColor(buttonQueryData);
        } else if (botStatus.equals(BotStatus.ASK_SIZE)) {
            tmpParsedItem.getTempItemOptions().setSize(buttonQueryData);
        } else if (botStatus.equals(BotStatus.ASK_GROUP)) {
            tmpParsedItem.getTempItemOptions().setGroup(buttonQueryData);
        }
        userProfile.setTmpParsedItem(tmpParsedItem);
        userRepository.saveUserProfile(userId, userProfile);

        update.setMessage(message);
    }

    // input message handler
    private BotApiMethod<?> handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long userId = message.getFrom().getId();
        BotStatus botStatus;
        BotStatus userRepositoryBotStatus = userRepository.getBotStatus(userId);

        if (userRepositoryBotStatus == null || inputMessage.equals(localeMessageService.getMessage("button.menu.showMenu"))) {
            botStatus = BotStatus.SHOW_MENU;
        } else if (userRepositoryBotStatus == BotStatus.SHOW_SEARCH || inputMessage.equals(localeMessageService.getMessage("button.menu.showSearch"))) {
            botStatus = BotStatus.SHOW_SEARCH;
        } else if (userRepositoryBotStatus == BotStatus.SHOW_PARSE || inputMessage.equals(localeMessageService.getMessage("button.menu.showParse"))) {
            botStatus = BotStatus.SHOW_PARSE;
        } else if (userRepositoryBotStatus == BotStatus.ASK_COLOR) {
            if (userRepository.getUserProfile(userId).getTmpParsedItem().getItemOptionsList().stream().anyMatch(color -> color.getColor().equals(inputMessage))) {
                botStatus = BotStatus.ASK_SIZE;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                makeSomePause(1000);
                botStatus = userRepositoryBotStatus;
            }
        } else if (userRepositoryBotStatus == BotStatus.ASK_SIZE) {
            if (userRepository.getUserProfile(userId).getTmpParsedItem().getItemOptionsList().stream().anyMatch(size -> size.getSize().equals(inputMessage))) {
                botStatus = BotStatus.ASK_TRACKING;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                makeSomePause(1000);
                botStatus = userRepositoryBotStatus;
            }
        } else if (userRepositoryBotStatus == BotStatus.ASK_TRACKING) {
            if (inputMessage.equals(localeMessageService.getMessage("reply.answer.yes"))) {
                botStatus = BotStatus.SHOW_PARSE_END;
            } else if (inputMessage.equals(localeMessageService.getMessage("reply.answer.no"))) {
                botStatus = BotStatus.SHOW_MENU;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                botStatus = BotStatus.ASK_TRACKING;
            }
        } else if (userRepositoryBotStatus == BotStatus.ASK_GROUP) {
            botStatus = BotStatus.ASK_TRACKING;
        } else if (userRepositoryBotStatus == BotStatus.SHOW_PARSE_END) {
            botStatus = BotStatus.SHOW_PARSE;
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

    private void makeSomePause(final int timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
