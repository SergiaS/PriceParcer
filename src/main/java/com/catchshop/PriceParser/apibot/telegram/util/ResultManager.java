package com.catchshop.PriceParser.apibot.telegram.util;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
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

    public void showItemFormattedResults(String chatId, ParseItem parseItem) {
        log.info("Item results: {}", parseItem);

        StringBuilder result = new StringBuilder();
        result.append("<u>").append("<a href=\"").append(parseItem.getUrl()).append("\">").append(parseItem.getTitle())
                .append("</a> [").append(parseItem.getRangePrice()).append("]</u>").append("\n");

        addOptions(parseItem, result);

        sendHtmlResultToTelegram(chatId, result.toString());
    }

    public void sendParseItemFormattedResults(String chatId, List<ParseItem> parseItemList) {
        log.info("ParseItem formatted results for shop {}, Total № of items: {}", parseItemList.get(0).getShop(), parseItemList.size());

        int count = 1;
        StringBuilder result = new StringBuilder();
        for (ParseItem parseItem : parseItemList) {
            if (parseItem == null) {
                log.error("There is an error with item={}", parseItem);
            } else {
                result.append("<u>").append(count).append(" <a href=\"").append(parseItem.getUrl()).append("\">").append(parseItem.getTitle())
                        .append("</a> [").append(parseItem.getRangePrice()).append("]</u>").append("\n");
                count++;

                addOptions(parseItem, result);

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
            result.append("\uD83D\uDCCC ").append(count++).append(" :: ").append(favoriteItem.getShop().getName()).append(" <a href=\"").append(favoriteItem.getUrl()).append("\">").append(favoriteItem.getTitle())
                    .append("</a>").append("\n").append("\uD83E\uDDED ").append(favoriteItem.getShop().getChosenCurrency()).append(favoriteItem.getOptions().getPrice()).append(", ");

            if (favoriteItem.getOptions().getGroup() != null) {
                result.append(favoriteItem.getOptions().getGroup());
            } else {
                result.append(favoriteItem.getOptions().getColor()).append(", ").append(favoriteItem.getOptions().getSize());
            }
            result.append(", ").append(favoriteItem.getOptions().getStatus()).append("\n").append("\n");

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

    private void addOptions(ParseItem parseItem, StringBuilder result) {
        for (ItemOptions options : parseItem.getItemOptionsList()) {
            result.append("<b>").append(parseItem.getShop().getChosenCurrency()).append(options.getPrice()).append("</b>");
            if (options.getColor() == null) {
                result
                        .append(options.getGroup() == null ? "" : ", " + options.getGroup())
                        .append(options.getStatus() == null ? "" : ", " + options.getStatus());
            } else {
                result
                        .append(options.getColor() == null ? "" : ", " + options.getColor())
                        .append(options.getSize() == null ? "" : ", " + options.getSize())
                        .append(options.getStatus() == null ? "" : ", " + options.getStatus());
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

    public void notifyIfItemUpdated(String chatId, FavoriteItem oldItem, FavoriteItem newItem) {
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
            } else {
                result.append("❌ ").append(newItem.getTitle());
            }
        }
        log.info(result.toString());
    }
}

