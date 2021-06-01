package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.ParseMenuService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.util.FormattedResult;
import com.catchshop.PriceParser.bike.model.Item;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.shops.bike24.Bike24Parser;
import com.catchshop.PriceParser.bike.shops.wiggle.WiggleParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class ParseMessageHandler implements InputMessageHandler {
    private LocaleMessageService localeMessageService;
    private ReplyMessageService replyMessageService;
    private PriceParserTelegramBot telegramBot;
    private ParseMenuService parseMenuService;
    private UserRepository userRepository;
    private FormattedResult formattedResult;
    private List<String> urlShopsList;

    @Autowired
    public ParseMessageHandler(LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, @Lazy PriceParserTelegramBot telegramBot, ParseMenuService parseMenuService, UserRepository userRepository, FormattedResult formattedResult) {
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.telegramBot = telegramBot;
        this.parseMenuService = parseMenuService;
        this.userRepository = userRepository;
        this.formattedResult = formattedResult;

        this.urlShopsList = new ArrayList<>();
        urlShopsList.add("https://www.wiggle.co.uk/");
        urlShopsList.add("https://www.bike24.com/");
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    private SendMessage processUserInput(Message inputMessage) {
        String userMsg = inputMessage.getText();
        String chatId = inputMessage.getChatId().toString();
        Long userId = inputMessage.getFrom().getId();

        BotStatus botStatus = userRepository.getUserProfile(userId).getBotStatus();

        SendMessage replyToUser = parseMenuService.getParseMenu(chatId, userMsg);
        replyToUser.disableWebPagePreview();

        if (botStatus.equals(BotStatus.SHOW_PARSE)) {
            if (userMsg.equals(localeMessageService.getMessage("button.menu.showParse"))) {
                replyToUser.setText(String.format(localeMessageService.getMessage("reply.menu.showParse"), showListOfShops()));
            } else if (isBikeShopUrl(userMsg)) {
                telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId, "reply.parse.start"));

                Item parseItemInfo = null;
                if (userMsg.contains("wiggle")) {
                    WiggleParser wiggleParser = new WiggleParser();
                    parseItemInfo = wiggleParser.parseItemInfo(userMsg);
                } else if (userMsg.contains("bike24")) {
                    Bike24Parser bike24Parser = new Bike24Parser();
                    parseItemInfo = bike24Parser.parseItemInfo(userMsg);
                }

                if (parseItemInfo == null) {
                    replyToUser = replyMessageService.getReplyMessage(chatId, "reply.notFound");
                } else {
                    formattedResult.showItemFormattedResults(chatId, parseItemInfo);

                    UserProfile userProfile = userRepository.getUserProfile(userId);
                    userProfile.setTmpParsedItem(parseItemInfo);

                    ItemOptions itemOptions = parseItemInfo.getItemOptionsList().get(0);
                    if (itemOptions.getGroup() != null) {
                        botStatus = BotStatus.ASK_GROUP;
                    } else if (itemOptions.getColor() != null) {
                        botStatus = BotStatus.ASK_COLOR;
                    } else {
                        botStatus = BotStatus.ASK_TRACKING;
                    }
                    userProfile.setBotStatus(botStatus);
                    userRepository.saveUserProfile(Long.valueOf(chatId), userProfile);
                }
            } else {
                replyToUser.setText(localeMessageService.getMessage("reply.parse.error"));
            }
        }

        UserProfile userProfile = userRepository.getUserProfile(userId);
        Item tmpParsedItem = userProfile.getTmpParsedItem();
        if (isFillingItem(botStatus)) {
            if (tmpParsedItem.getItemOptionsList().get(0).getGroup() == null) {
                if (botStatus.equals(BotStatus.ASK_COLOR)) {
                    replyToUser.setText(localeMessageService.getMessage("reply.parse.askColor"));
                    replyToUser.setReplyMarkup(getInlineOptionButtons(botStatus, tmpParsedItem));
                } else if (botStatus.equals(BotStatus.ASK_SIZE)) {
                    replyToUser.setText(localeMessageService.getMessage("reply.parse.askSize"));
                    replyToUser.setReplyMarkup(getInlineOptionButtons(botStatus, tmpParsedItem));
                }
            } else /* if (botStatus.equals(BotStatus.ASK_GROUP)) */ {
                replyToUser.setText(localeMessageService.getMessage("reply.parse.askOption"));
                replyToUser.setReplyMarkup(getInlineOptionButtons(botStatus, tmpParsedItem));
            }
        } else if (botStatus.equals(BotStatus.ASK_TRACKING)) {
            replyToUser.setText(String.format(localeMessageService.getMessage("reply.parse.askAddToFavorite"), getItemNameWithOptions(tmpParsedItem)));
            replyToUser.setReplyMarkup(getInlineTrackingAnswerButtons());
            replyToUser.enableHtml(true);
        }
        // show results and save to favorite
        else if (botStatus.equals(BotStatus.SHOW_PARSE_END)) {
            String result = String.format(localeMessageService.getMessage("reply.parse.end"), getItemNameWithOptions(tmpParsedItem));
            replyToUser = parseMenuService.getParseMenu(chatId, userMsg);
            replyToUser.enableHtml(true);
            replyToUser.setText(result);

            tmpParsedItem.setTempItemOptions(null);
            userProfile.setBotStatus(BotStatus.SHOW_MENU);
            userProfile.setTmpParsedItem(tmpParsedItem);
            userRepository.saveUserProfile(userId, userProfile);


            System.out.println(" >>> need to store the result to favorite");
        }
        return replyToUser;
    }

    private String getItemNameWithOptions(Item tmpParsedItem) {
        String title = tmpParsedItem.getTitle();
        StringBuilder result = new StringBuilder(title);
        if (tmpParsedItem.getTempItemOptions().getGroup() != null) {
            String group = tmpParsedItem.getTempItemOptions().getGroup();
            result.append(", ").append(group);
        } else if (tmpParsedItem.getTempItemOptions().getColor() != null) {
            String color = tmpParsedItem.getTempItemOptions().getColor();
            String size = tmpParsedItem.getTempItemOptions().getSize();
            result.append(", ").append(color).append(", ").append(size);
        }
        return result.toString();
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_PARSE;
    }

    private boolean isBikeShopUrl(String userText) {
        for (String urlShop : urlShopsList) {
            if (userText.startsWith(urlShop)) {
                return true;
            }
        }
        return false;
    }

    private String showListOfShops() {
        StringBuilder sb = new StringBuilder();
        for (String urlShop : urlShopsList) {
            sb.append("\n").append(urlShop);
        }
        return sb.append("\n").toString();
    }

    private InlineKeyboardMarkup getInlineTrackingAnswerButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        String textYes = localeMessageService.getMessage("reply.answer.yes");
        String textNo = localeMessageService.getMessage("reply.answer.no");
        InlineKeyboardButton buttonYes = new InlineKeyboardButton(textYes);
        InlineKeyboardButton buttonNo = new InlineKeyboardButton(textNo);
        buttonYes.setCallbackData(textYes);
        buttonNo.setCallbackData(textNo);

        List<InlineKeyboardButton> buttonsList = new ArrayList<>();
        buttonsList.add(buttonYes);
        buttonsList.add(buttonNo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(buttonsList);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


    private InlineKeyboardMarkup getInlineOptionButtons(BotStatus botStatus, Item tmpParsedItem) {
        List<ItemOptions> optionsList = tmpParsedItem.getItemOptionsList();

        Set<String> set = new LinkedHashSet<>();
        if (botStatus.equals(BotStatus.ASK_COLOR)) {
            for (ItemOptions itemOptions : optionsList) {
                set.add(itemOptions.getColor());
            }
        } else if (botStatus.equals(BotStatus.ASK_SIZE)) {
            String selectedColor = tmpParsedItem.getTempItemOptions().getColor();
            for (ItemOptions itemOptions : optionsList) {
                // добавляем доступные размеры для выбранного цвета
                if (itemOptions.getColor().equals(selectedColor)) {
                    set.add(itemOptions.getSize());
                }
            }
        } else if (botStatus.equals(BotStatus.ASK_GROUP)) {
            for (ItemOptions itemOptions : optionsList) {
                set.add(itemOptions.getGroup());
            }
        }

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> buttonsList = new ArrayList<>();
        for (String s : set) {
            InlineKeyboardButton button = new InlineKeyboardButton(s);
            button.setCallbackData(s);
            if (buttonsList.size() == 2) {
                rowList.add(buttonsList);
                buttonsList = new ArrayList<>();
            }
            buttonsList.add(button);
        }
        if (buttonsList.size() > 0) {
            rowList.add(buttonsList);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private boolean isFillingItem(BotStatus botStatus) {
        switch (botStatus) {
            case ASK_COLOR:
            case ASK_SIZE:
            case ASK_GROUP:
                return true;
            default:
                return false;
        }
    }
}
