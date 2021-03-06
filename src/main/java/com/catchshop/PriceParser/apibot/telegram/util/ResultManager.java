package com.catchshop.PriceParser.apibot.telegram.util;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class ResultManager {

    private final PriceParserTelegramBot telegramBot;
    private final LocaleMessageService localeMessageService;

    @Autowired
    public ResultManager(@Lazy PriceParserTelegramBot telegramBot, LocaleMessageService localeMessageService) {
        this.telegramBot = telegramBot;
        this.localeMessageService = localeMessageService;
    }

    public void showItemFormattedResults(String chatId, ParsedItem parsedItem) {
        log.info("Item results: {}", parsedItem);

        StringBuilder result = new StringBuilder();
        result.append("<u>").append("<a href=\"").append(parsedItem.getUrl()).append("\">").append(parsedItem.getTitle())
                .append("</a> [").append(parsedItem.getRangePrice()).append("]</u>").append("\n");

        addOptions(parsedItem, result);

        sendHtmlResultToTelegram(chatId, result.toString());
    }

    public void sendParseItemFormattedResults(String chatId, List<ParsedItem> parsedItemList) {
        log.info("ParseItem formatted results for shop {}, Total № of items: {}", parsedItemList.get(0).getShop(), parsedItemList.size());

        int count = 1;
        StringBuilder result = new StringBuilder();
        for (ParsedItem parsedItem : parsedItemList) {
            if (parsedItem == null) {
                log.error("There is an error with item={}", parsedItem);
            } else {
                result.append("<u>").append(count).append(" <a href=\"").append(parsedItem.getUrl()).append("\">").append(parsedItem.getTitle())
                        .append("</a> [").append(parsedItem.getRangePrice()).append("]</u>").append("\n");
                count++;

                addOptions(parsedItem, result);

                sendIfResultIsTooBig(chatId, result);
            }
        }
        if (result.length() > 0) {
            sendHtmlResultToTelegram(chatId, result.toString());
        }
    }

    public SendMessage getFavoriteItemFormattedResult(String chatId, List<FavoriteItem> favorites) {
        log.info("FavoriteItem formatted results. Total № of items: {}", favorites.size());

        StringBuilder result = new StringBuilder(localeMessageService.getMessage("reply.menu.showFavorites"));

        favorites.sort(Comparator.comparing(x -> x.getShop().getName()));

        int count = 1;
        for (FavoriteItem favoriteItem : favorites) {
            String group = favoriteItem.getOptions().getGroup();
            String color = favoriteItem.getOptions().getColor();
            String size = favoriteItem.getOptions().getSize();
            String status = favoriteItem.getOptions().getStatus();
            BigDecimal price = favoriteItem.getOptions().getPrice();

            result.append("\uD83D\uDCCC ").append(count++).append(" :: ").append(favoriteItem.getShop().getName()).append(" <a href=\"").append(favoriteItem.getUrl()).append("\">").append(favoriteItem.getTitle())
                    .append("</a>").append("\n").append("\uD83E\uDDED ").append(favoriteItem.getShop().getChosenCurrency()).append(price);

            if (group != null) {
                result.append(", ").append(group);
            } else if (color != null && size != null) {
                result.append(", ").append(color).append(", ").append(size);
            }
            result.append(", ").append(status).append("\n").append("\n");

            sendIfResultIsTooBig(chatId, result);
        }
        SendMessage message = new SendMessage(chatId, result.toString());
        message.enableHtml(true);
        message.setDisableWebPagePreview(true);
        return message;
    }

    private void sendIfResultIsTooBig(String chatId, StringBuilder result) {
        if (result.length() > 3000) {
            sendHtmlResultToTelegram(chatId, result.toString());
            result.setLength(0);
        }
    }

    private void addOptions(ParsedItem parsedItem, StringBuilder result) {
        for (ItemOptions options : parsedItem.getParsedOptionsList()) {
            result.append("<b>").append(parsedItem.getShop().getChosenCurrency()).append(options.getPrice()).append("</b>");
            if (options.getColor() == null) {
                result
                        .append(options.getGroup() == null || options.getGroup().isBlank() ? "" : ", " + options.getGroup())
                        .append(options.getStatus() == null || options.getStatus().isBlank() ? "" : ", " + options.getStatus());
            } else {
                result
                        .append(options.getColor() == null || options.getColor().isBlank() ? "" : ", " + options.getColor())
                        .append(options.getSize() == null || options.getSize().isBlank() ? "" : ", " + options.getSize())
                        .append(options.getStatus() == null || options.getStatus().isBlank() ? "" : ", " + options.getStatus());
            }
            result.append("\n");
        }
    }

    public void sendHtmlResultToTelegram(String chatId, String result) {
        SendMessage resMsg = new SendMessage(chatId, result);
        resMsg.enableHtml(true);
        resMsg.setDisableWebPagePreview(true);
        telegramBot.sendMessage(resMsg);
    }

    public boolean notifyIfItemUpdated(String chatId, FavoriteItem oldItem, FavoriteItem newItem) {
        boolean isUpdated = false;
        StringBuilder result = new StringBuilder();
        if (!oldItem.getTitle().equals(newItem.getTitle()) && !oldItem.getShop().equals(newItem.getShop())) {
            result.append(localeMessageService.getMessage("reply.favorites.changedDifferenceError")).append(" ").append(newItem);
        } else if (newItem.getOptions() == null) {
            result.append(localeMessageService.getMessage("reply.favorites.changedParameterError")).append(" ").append(newItem);
        } else {
            ItemOptions oldItemOptions = oldItem.getOptions();
            ItemOptions newItemOptions = newItem.getOptions();
            BigDecimal oldPrice = oldItemOptions.getPrice();
            BigDecimal newPrice = newItemOptions.getPrice();
            if (!oldPrice.equals(newPrice)) {
                BigDecimal priceDifference = newPrice.subtract(oldPrice);
                String priceDirectionSymbol = priceDifference.compareTo(BigDecimal.ZERO) > 0 ? " ⬆ +" : " ⬇ ";
                result.append(String.format(localeMessageService.getMessage("reply.favorites.changedPrice"), oldPrice, newPrice))
                        .append(priceDirectionSymbol).append(priceDifference).append(oldItem.getShop().getChosenCurrency());
            }

            String oldStatus = oldItemOptions.getStatus();
            String newStatus = newItemOptions.getStatus();
            if (!oldStatus.equals(newStatus)) {
                result.append(String.format(localeMessageService.getMessage("reply.favorites.changedStatus"), oldStatus, newStatus));
            }

            if (result.length() != 0) {
                result.append("\n").insert(0,
                        String.format(localeMessageService.getMessage("reply.favorites.changed"), newItem.getShop().getName(), newItem.getUrl(), newItem.getTitle(),
                                newItem.getShop().getChosenCurrency() + newItem.getOptions().getPrice(),
                                newItem.getOptions().getGroup() != null ? newItem.getOptions().getGroup() : newItem.getOptions().getSize() + ", " + newItem.getOptions().getColor(),
                                newItem.getOptions().getStatus()));
                sendHtmlResultToTelegram(chatId, result.toString());
                isUpdated = true;
            } else {
                result.append("❌ ").append("NO CHANGES for chatId: ").append(chatId).append(", ").append("SHOP: ").append(newItem.getShop().getName()).append(", ")
                        .append("ITEM: ").append(newItem.getTitle());
            }
        }
        log.info(result.toString());
        return isUpdated;
    }
}

