package com.catchshop.PriceParser.apibot.telegram.api;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.UserProfileService;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class TelegramStart {
    private final BotStatusContext botStatusContext;
    private final UserProfileService userProfileService;
    private final LocaleMessageService localeMessageService;
    private final PriceParserTelegramBot telegramBot;

    @Autowired
    public TelegramStart(BotStatusContext botStatusContext, UserProfileService userProfileService, LocaleMessageService localeMessageService, @Lazy PriceParserTelegramBot telegramBot) {
        this.botStatusContext = botStatusContext;
        this.userProfileService = userProfileService;
        this.localeMessageService = localeMessageService;
        this.telegramBot = telegramBot;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}",
                    callbackQuery.getFrom().getUserName(), callbackQuery.getFrom().getId(), callbackQuery.getData());
            addMessageToUpdate(update);
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

    private void addMessageToUpdate(Update update) {
        CallbackQuery buttonQuery = update.getCallbackQuery();
        String buttonQueryData = buttonQuery.getData();

        Message message = buttonQuery.getMessage();
        message.setFrom(buttonQuery.getFrom());
        message.setText(buttonQueryData);

        update.setMessage(message);
    }

    // input message handler
    private BotApiMethod<?> handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();

        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        if (userProfile == null) {
            userProfile = userProfileService.saveUserProfile(new UserProfile(chatId));
        }
        BotStatus profileBotStatus = userProfile.getBotStatus();

        BotStatus botStatus;
        if (inputMessage.equals(localeMessageService.getMessage("button.menu.showMenu"))) {
            botStatus = BotStatus.SHOW_MENU;
        } else if (profileBotStatus == BotStatus.SHOW_SEARCH || inputMessage.equals(localeMessageService.getMessage("button.menu.showSearch"))) {
            botStatus = BotStatus.SHOW_SEARCH;
        } else if (profileBotStatus == BotStatus.SHOW_PARSE || inputMessage.equals(localeMessageService.getMessage("button.menu.showParse"))) {
            botStatus = BotStatus.SHOW_PARSE;
        } else if (profileBotStatus == BotStatus.ASK_COLOR) {
            if (userProfile.getTmpParsedItem().getItemOptionsList().stream().anyMatch(color -> color.getColor().equals(inputMessage))) {
                saveUserChoice(inputMessage, userProfile);
                botStatus = BotStatus.ASK_SIZE;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                makeSomePause(1000);
                botStatus = profileBotStatus;
            }
        } else if (profileBotStatus == BotStatus.ASK_SIZE) {
            if (userProfile.getTmpParsedItem().getItemOptionsList().stream().anyMatch(size -> size.getSize().equals(inputMessage))) {
                saveUserChoice(inputMessage, userProfile);
                botStatus = BotStatus.ASK_TRACKING;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                makeSomePause(1000);
                botStatus = profileBotStatus;
            }
        } else if (profileBotStatus == BotStatus.ASK_GROUP) {
            if (userProfile.getTmpParsedItem().getItemOptionsList().stream().anyMatch(size -> size.getGroup().equals(inputMessage))) {
                saveUserChoice(inputMessage, userProfile);
                botStatus = BotStatus.ASK_TRACKING;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                makeSomePause(1000);
                botStatus = profileBotStatus;
            }
        } else if (profileBotStatus == BotStatus.ASK_TRACKING) {
            if (inputMessage.equals(localeMessageService.getMessage("reply.answer.yes"))) {
                botStatus = BotStatus.SHOW_PARSE_END;
            } else if (inputMessage.equals(localeMessageService.getMessage("reply.answer.no"))) {
                botStatus = BotStatus.SHOW_MENU;
            } else {
                telegramBot.sendMessage(new SendMessage(userId.toString(), localeMessageService.getMessage("reply.parse.wrongOption")));
                botStatus = BotStatus.ASK_TRACKING;
            }
        } else if (profileBotStatus == BotStatus.SHOW_PARSE_END) {
            botStatus = BotStatus.SHOW_PARSE;
        } else if (profileBotStatus == BotStatus.SHOW_FAVORITES && inputMessage.equals(localeMessageService.getMessage("button.menu.deleteFavoriteByNumber")) ||
                profileBotStatus == BotStatus.SHOW_FAVORITES_DELETE) {
            botStatus = BotStatus.SHOW_FAVORITES_DELETE;
        } else if (profileBotStatus == BotStatus.SHOW_FAVORITES || inputMessage.equals(localeMessageService.getMessage("button.menu.showFavorites"))) {
            botStatus = BotStatus.SHOW_FAVORITES;
        } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.showLanguages"))) {
            botStatus = BotStatus.SHOW_LANGUAGES;
        } // changing locale
        else if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.english")) ||
                inputMessage.equals(localeMessageService.getMessage("button.menu.language.ukrainian")) ||
                inputMessage.equals(localeMessageService.getMessage("button.menu.language.russian"))) {
            if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.english"))) {
                userProfile.setLanguageTag("en-EN");
            } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.ukrainian"))) {
                userProfile.setLanguageTag("ua-UA");
            } else if (inputMessage.equals(localeMessageService.getMessage("button.menu.language.russian"))) {
                userProfile.setLanguageTag("ru-RU");
            }
            botStatus = BotStatus.SHOW_MENU;
            localeMessageService.setLanguageTag(userProfile.getLanguageTag());
        } // error zone
        else {
            botStatus = BotStatus.SHOW_ERROR;
            System.out.println("BOOM in TelegramStart");
        }
        userProfile.setBotStatus(botStatus);
        userProfileService.saveUserProfile(userProfile);
        return botStatusContext.processInputMessage(botStatus, message);
    }

    private void saveUserChoice(String userChoice, UserProfile userProfile) {
        BotStatus botStatus = userProfile.getBotStatus();
        ParseItem tmpParsedParseItem = userProfile.getTmpParsedItem();

        if (botStatus.equals(BotStatus.ASK_COLOR)) {
            tmpParsedParseItem.getOptions().setColor(userChoice);
        } else if (botStatus.equals(BotStatus.ASK_SIZE)) {
            tmpParsedParseItem.getOptions().setSize(userChoice);
            for (ItemOptions itemOptions : tmpParsedParseItem.getItemOptionsList()) {
                if (itemOptions.getColor().equals(tmpParsedParseItem.getOptions().getColor()) &&
                        itemOptions.getSize().equals(tmpParsedParseItem.getOptions().getSize())) {
                    tmpParsedParseItem.getOptions().setStatus(itemOptions.getStatus());
                    tmpParsedParseItem.getOptions().setPrice(itemOptions.getPrice());
                }
            }
        } else if (botStatus.equals(BotStatus.ASK_GROUP)) {
            tmpParsedParseItem.getOptions().setGroup(userChoice);
            for (ItemOptions itemOptions : tmpParsedParseItem.getItemOptionsList()) {
                if (itemOptions.getGroup().equals(tmpParsedParseItem.getOptions().getGroup())) {
                    tmpParsedParseItem.getOptions().setStatus(itemOptions.getStatus());
                    tmpParsedParseItem.getOptions().setPrice(itemOptions.getPrice());
                }
            }
        }
        userProfile.setTmpParsedItem(tmpParsedParseItem);
    }

    private void makeSomePause(final int timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
