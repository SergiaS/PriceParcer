package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MenuKeyboardService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.UserProfileService;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.shops.MainParser;
import com.catchshop.PriceParser.bike.util.ShopHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class ParseMessageHandler implements InputMessageHandler {
    private final MenuKeyboardService menuKeyboardService;
    private final LocaleMessageService localeMessageService;
    private final ReplyMessageService replyMessageService;
    private final PriceParserTelegramBot telegramBot;
    private final UserProfileService userProfileService;
    private final ResultManager resultManager;
    private final List<String> urlShopsList;

    @Autowired
    public ParseMessageHandler(LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, @Lazy PriceParserTelegramBot telegramBot, MenuKeyboardService menuKeyboardService, UserProfileService userProfileService, ResultManager resultManager) {
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.telegramBot = telegramBot;
        this.menuKeyboardService = menuKeyboardService;
        this.userProfileService = userProfileService;
        this.resultManager = resultManager;

        this.urlShopsList = new ArrayList<>();
        urlShopsList.add("https://www.wiggle.co.uk/");
        urlShopsList.add("https://www.bike24.com/");
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        String userMsg = inputMessage.getText();
        Long chatId = inputMessage.getChatId();

        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId.toString());
        replyToUser.disableWebPagePreview();
        replyToUser.enableHtml(true);

        mainParseAction(chatId, replyToUser, userMsg);

        tmpParsedItemAction(chatId, replyToUser);

        return replyToUser;
    }

    private void mainParseAction(Long chatId, SendMessage replyToUser, String userMsg) {
        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        BotStatus botStatus = userProfile.getBotStatus();
        if (botStatus.equals(BotStatus.SHOW_PARSE)) {
            if (userMsg.equals(localeMessageService.getMessage("button.menu.showParse"))) {
                replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
                replyToUser.setText(String.format(localeMessageService.getMessage("reply.menu.showParse"), showListOfShops()));
            } else if (isBikeShopUrl(userMsg)) {
                telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId.toString(), "reply.parse.start"));

                ParseItem parseItemInfo;
                MainParser parser = ShopHelper.storeIdentifier(userMsg);
                parseItemInfo = parser.parseItemInfo(userMsg);

                if (parseItemInfo == null) {
                    replyToUser.setText(localeMessageService.getMessage("reply.notFound"));
                    botStatus = BotStatus.SHOW_PARSE_END;
                } else {
                    resultManager.showItemFormattedResults(chatId.toString(), parseItemInfo);

                    ItemOptions itemOptions = parseItemInfo.getItemOptionsList().get(0);

                    // if there is only one variation of item - suggest to save it
                    if (parseItemInfo.getItemOptionsList().size() == 1) {
                        botStatus = BotStatus.ASK_TRACKING;
                    } else if (itemOptions.getGroup() != null) {
                        botStatus = BotStatus.ASK_GROUP;
                    } else if (itemOptions.getColor() != null) {
                        botStatus = BotStatus.ASK_COLOR;
                    } else {
                        botStatus = BotStatus.ASK_TRACKING;
                    }
                    userProfile.setTmpParsedItem(parseItemInfo);
                }
                userProfile.setBotStatus(botStatus);
                userProfileService.saveUserProfile(userProfile);
            } else {
                replyToUser.setText(localeMessageService.getMessage("reply.parse.error"));
            }
        }
    }

    private void tmpParsedItemAction(Long chatId, SendMessage replyToUser) {
        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        ParseItem tmpParsedParseItem = userProfile.getTmpParsedItem();
        BotStatus botStatus = userProfile.getBotStatus();
        if (isFillingItem(botStatus)) {
            if (tmpParsedParseItem.getItemOptionsList().get(0).getGroup() == null) {
                if (botStatus.equals(BotStatus.ASK_COLOR)) {
                    replyToUser.setText(localeMessageService.getMessage("reply.parse.askColor"));
                } else if (botStatus.equals(BotStatus.ASK_SIZE)) {
                    replyToUser.setText(localeMessageService.getMessage("reply.parse.askSize"));
                }
            } else /* if (botStatus.equals(BotStatus.ASK_GROUP)) */ {
                replyToUser.setText(localeMessageService.getMessage("reply.parse.askOption"));
            }
            replyToUser.setReplyMarkup(getInlineOptionButtons(botStatus, tmpParsedParseItem));
        } else if (botStatus.equals(BotStatus.ASK_TRACKING)) {
            replyToUser.setText(String.format(localeMessageService.getMessage("reply.parse.askAddToFavorite"), getItemNameWithOptions(tmpParsedParseItem)));
            replyToUser.setReplyMarkup(getInlineTrackingAnswerButtons());
        }
        // show results and save to favorite
        else if (botStatus.equals(BotStatus.SHOW_PARSE_END)) {
            userProfile.setBotStatus(BotStatus.SHOW_MENU);
            if (tmpParsedParseItem != null) {
                String result = String.format(localeMessageService.getMessage("reply.parse.end"), getItemNameWithOptions(tmpParsedParseItem));
                replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
                replyToUser.setText(result);

                userProfile.getFavorites().add(FavoriteItem.convertToFavoriteItem(tmpParsedParseItem));
                userProfile.setTmpParsedItem(null);
            }
            userProfileService.saveUserProfile(userProfile);
        }
    }

    private String getItemNameWithOptions(ParseItem tmpParsedParseItem) {
        String title = tmpParsedParseItem.getTitle();
        StringBuilder result = new StringBuilder(title);
        if (tmpParsedParseItem.getOptions().getGroup() != null) {
            String group = tmpParsedParseItem.getOptions().getGroup();
            result.append(", ").append(group);
        } else if (tmpParsedParseItem.getOptions().getColor() != null) {
            String color = tmpParsedParseItem.getOptions().getColor();
            String size = tmpParsedParseItem.getOptions().getSize();
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

    private InlineKeyboardMarkup getInlineOptionButtons(BotStatus botStatus, ParseItem tmpParsedParseItem) {
        List<ItemOptions> optionsList = tmpParsedParseItem.getItemOptionsList();

        Set<String> set = new LinkedHashSet<>();
        if (botStatus.equals(BotStatus.ASK_COLOR)) {
            for (ItemOptions itemOptions : optionsList) {
                set.add(itemOptions.getColor());
            }
        } else if (botStatus.equals(BotStatus.ASK_SIZE)) {
            String selectedColor = tmpParsedParseItem.getOptions().getColor();
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
