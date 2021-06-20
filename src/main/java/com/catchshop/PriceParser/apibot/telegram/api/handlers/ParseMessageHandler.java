package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.CachedParsedResult;
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

import java.math.BigDecimal;
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
    private final CachedParsedResult cachedParsedResult;
    private final List<String> urlShopsList;

    @Autowired
    public ParseMessageHandler(LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, @Lazy PriceParserTelegramBot telegramBot, MenuKeyboardService menuKeyboardService, UserProfileService userProfileService, ResultManager resultManager, CachedParsedResult cachedParsedResult) {
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.telegramBot = telegramBot;
        this.menuKeyboardService = menuKeyboardService;
        this.userProfileService = userProfileService;
        this.resultManager = resultManager;
        this.cachedParsedResult = cachedParsedResult;

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

        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        BotStatus botStatus = userProfile.getBotStatus();
        if (botStatus.equals(BotStatus.SHOW_PARSE)) {
            if (userMsg.equals(localeMessageService.getMessage("button.menu.showParse"))) {
                replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
                replyToUser.setText(String.format(localeMessageService.getMessage("reply.menu.showParse"), showListOfShops()));
            } else if (isBikeShopUrl(userMsg)) {
                telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId.toString(), "reply.parse.start"));

                ParsedItem parsedItem;
                MainParser parser = ShopHelper.storeIdentifier(userMsg);
                parsedItem = parser.parseItemInfo(userMsg);

                if (parsedItem == null) {
                    replyToUser.setText(localeMessageService.getMessage("reply.notFound"));
                    botStatus = BotStatus.SHOW_MENU;
                    userProfile.setBotStatus(botStatus);
                    userProfileService.saveUserProfile(userProfile);
                } else {
                    resultManager.showItemFormattedResults(chatId.toString(), parsedItem);
                    cachedParsedResult.addParsedItem(chatId, parsedItem);
                    askToSelectOption(chatId, replyToUser);
                }
            } else {
                replyToUser.setText(localeMessageService.getMessage("reply.parse.error"));
            }
        } else if (botStatus == BotStatus.FILLING_ITEM) {
            ParsedItem parsedItem = cachedParsedResult.getParsedItem(chatId);
            if (parsedItem == null) {
                System.out.println("error with ParsedItem in botStatus == BotStatus.FILLING_ITEM");
                replyToUser.setText(localeMessageService.getMessage("reply.menu.showMenu"));
                replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
            }

            // user text validation
            List<ItemOptions> parsedItemOptionsList = parsedItem.getParsedOptionsList();
            boolean isColor = parsedItemOptionsList.stream().anyMatch(color -> userMsg.equals(color.getColor()));
            boolean isSize = parsedItemOptionsList.stream().anyMatch(size -> userMsg.equals(size.getSize()));
            boolean isGroup = parsedItemOptionsList.stream().anyMatch(group -> userMsg.equals(group.getGroup()));

            if (isColor || isSize || isGroup) {
                if (isColor) {
                    parsedItem.getSelectedOptions().setColor(userMsg);
                } else if (isSize) {
                    parsedItem.getSelectedOptions().setSize(userMsg);
                } else if (isGroup) {
                    parsedItem.getSelectedOptions().setGroup(userMsg);
                }
                askToSelectOption(chatId, replyToUser);
            } else {
                telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId.toString(), "reply.parse.wrongOption"));
                askToSelectOption(chatId, replyToUser);
            }
        } else if (botStatus.equals(BotStatus.ASK_TRACKING)) {
            if (userMsg.equals(localeMessageService.getMessage("reply.answer.yes"))) {
                ParsedItem parsedItem = cachedParsedResult.getParsedItem(chatId);

                userProfile.getFavorites().add(FavoriteItem.convertToFavoriteItem(parsedItem));
                userProfile.setBotStatus(BotStatus.SHOW_MENU);
                userProfileService.saveUserProfile(userProfile);

                String result = String.format(localeMessageService.getMessage("reply.parse.end"), getItemNameWithOptions(parsedItem));
                replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
                replyToUser.setText(result);

                cachedParsedResult.removeParsedItem(chatId);
            } else if (userMsg.equals(localeMessageService.getMessage("reply.answer.no"))) {
                userProfile.setBotStatus(BotStatus.SHOW_MENU);
                userProfileService.saveUserProfile(userProfile);
                replyToUser.setText(localeMessageService.getMessage("reply.menu.showMenu"));
                replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));

                cachedParsedResult.removeParsedItem(chatId);
            } else {
                telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId.toString(), "reply.parse.wrongOption"));
                askToSelectOption(chatId, replyToUser);
            }
        }
        return replyToUser;
    }

    private void askToSelectOption(Long chatId, SendMessage replyToUser) {
        ParsedItem parsedItem = cachedParsedResult.getParsedItem(chatId);

        ItemOptions parsedOptions = parsedItem.getParsedOptionsList().get(0); // at least 1 must be
        String parsedColor = parsedOptions.getColor();
        String parsedSize = parsedOptions.getSize();
        String parsedGroup = parsedOptions.getGroup();

        ItemOptions selectedOptions = parsedItem.getSelectedOptions();
        String selectedColor = selectedOptions.getColor();
        String selectedSize = selectedOptions.getSize();
        String selectedGroup = selectedOptions.getGroup();

        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        BotStatus botStatus = BotStatus.FILLING_ITEM;
        String message, buttonsList;
        if (parsedColor != null && selectedColor == null) {
            message = localeMessageService.getMessage("reply.parse.askColor");
            buttonsList = "color";
        } else if (parsedSize != null && selectedSize == null) {
            message = localeMessageService.getMessage("reply.parse.askSize");
            buttonsList = "size";
        } else if (parsedGroup != null && selectedGroup == null) {
            message = localeMessageService.getMessage("reply.parse.askOption");
            buttonsList = "group";
        } else {
            saveSelectedOptions(parsedItem);
            message = String.format(localeMessageService.getMessage("reply.parse.askAddToFavorite"), getItemNameWithOptions(parsedItem));
            buttonsList = "answer";
            botStatus = BotStatus.ASK_TRACKING;
        }
        userProfile.setBotStatus(botStatus);
        userProfileService.saveUserProfile(userProfile);
        replyToUser.setText(message);
        replyToUser.setReplyMarkup(getOptionsListButtons(parsedItem, buttonsList));
    }

    private void saveSelectedOptions(ParsedItem parsedItem) {
        String selectedColor = parsedItem.getSelectedOptions().getColor();
        String selectedSize = parsedItem.getSelectedOptions().getSize();
        String selectedGroup = parsedItem.getSelectedOptions().getGroup();

        for (ItemOptions options : parsedItem.getParsedOptionsList()) {
            String parsedColor = options.getColor();
            String parsedSize = options.getSize();
            String parsedGroup = options.getGroup();
            BigDecimal parsedPrice = options.getPrice();
            String parsedStatus = options.getStatus();
            if (selectedColor != null && selectedSize != null) {
                if (selectedColor.equals(parsedColor) && selectedSize.equals(parsedSize)) {
                    parsedItem.getSelectedOptions().setStatus(parsedStatus);
                    parsedItem.getSelectedOptions().setPrice(parsedPrice);
                    break;
                }
            } else if (selectedGroup != null) {
                if (selectedGroup.equals(parsedGroup)) {
                    parsedItem.getSelectedOptions().setStatus(parsedStatus);
                    parsedItem.getSelectedOptions().setPrice(parsedPrice);
                    break;
                }
            } else if (parsedColor == null && parsedSize == null && parsedGroup == null) {
                parsedItem.getSelectedOptions().setStatus(parsedStatus);
                parsedItem.getSelectedOptions().setPrice(parsedPrice);
                break;
            }
        }
    }

    private InlineKeyboardMarkup getOptionsListButtons(ParsedItem parsedItem, String targetOption) {
        Set<String> set = new LinkedHashSet<>();
        if (targetOption.equals("color")) {
            for (ItemOptions parsedOptions : parsedItem.getParsedOptionsList()) {
                set.add(parsedOptions.getColor());
            }
        } else if (targetOption.equals("size")) {
            String selectedColor = parsedItem.getSelectedOptions().getColor();
            for (ItemOptions parsedOptions : parsedItem.getParsedOptionsList()) {
                String parsedColor = parsedOptions.getColor();
                String parsedSize = parsedOptions.getSize();
                if (selectedColor != null && selectedColor.equals(parsedColor)) {
                    set.add(parsedSize);
                }
            }
        } else if (targetOption.equals("group")) {
            for (ItemOptions parsedOptions : parsedItem.getParsedOptionsList()) {
                String parsedGroup = parsedOptions.getGroup();
                set.add(parsedGroup);
            }
        } else if (targetOption.equals("answer")) {
            set.add(localeMessageService.getMessage("reply.answer.yes"));
            set.add(localeMessageService.getMessage("reply.answer.no"));
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

    private String getItemNameWithOptions(ParsedItem parsedItem) {
        StringBuilder result = new StringBuilder(parsedItem.getTitle());

        String color = parsedItem.getSelectedOptions().getColor();
        String size = parsedItem.getSelectedOptions().getSize();
        String group = parsedItem.getSelectedOptions().getGroup();
        BigDecimal price = parsedItem.getSelectedOptions().getPrice();
        String status = parsedItem.getSelectedOptions().getStatus();
        String currency = parsedItem.getShop().getChosenCurrency();

        if (color == null && size == null && group == null) {
            result
                    .append(price != null ? ", " + currency + price : "")
                    .append(status != null ? ", " + status : "");
        } else {
            result
                    .append(group != null ? ", " + group : "")
                    .append(color != null ? ", " + color : "")
                    .append(size != null ? ", " + size : "")
                    .append(price != null ? ", " + currency + price : "")
                    .append(status != null ? ", " + status : "");
        }
        return result.toString();
    }

    private boolean isBikeShopUrl(String userText) {
        for (String urlShop : urlShopsList) {
            if (userText.startsWith(urlShop)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_PARSE;
    }

    private String showListOfShops() {
        StringBuilder sb = new StringBuilder();
        for (String urlShop : urlShopsList) {
            sb.append("\n").append(urlShop);
        }
        return sb.append("\n").toString();
    }
}
