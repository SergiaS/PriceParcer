package com.catchshop.PriceParser.apibot.telegram.util;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.bike.model.Item;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
@Slf4j
public class FormattedResult {

    private final PriceParserTelegramBot telegramBot;

    @Autowired
    public FormattedResult(@Lazy PriceParserTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void showItemFormattedResults(String chatId, Item item) {
        log.info("Item results: {}", item);

        StringBuilder result = new StringBuilder();
        result.append("<u>").append("<a href=\"").append(item.getURL()).append("\">").append(item.getTitle())
                .append("</a> [").append(item.getRangePrice()).append("]</u>").append("\n");

        addOptions(item, result);

        sendResultToTelegram(chatId, result.toString());
    }

    public void showShopsFormattedResults(String chatId, List<Item> itemList) {
        log.info("Format {} shop results, Total № of items: {}", itemList.get(0).getShop(), itemList.size());

        int count = 1;
        StringBuilder result = new StringBuilder();
        for (Item item : itemList) {
            if (item == null) {
                log.error("There is an error with item={}", item);
            } else {
                result.append("<u>").append(count).append(" <a href=\"").append(item.getURL()).append("\">").append(item.getTitle())
                        .append("</a> [").append(item.getRangePrice()).append("]</u>").append("\n");
                count++;

                addOptions(item, result);

                if (result.length() > 3000) {
                    sendResultToTelegram(chatId, result.toString());
                    result.setLength(0);
                }
            }
        }
        sendResultToTelegram(chatId, result.toString());
    }

    private void addOptions(Item item, StringBuilder result) {
        for (ItemOptions options : item.getItemOptionsList()) {
            result.append("<b>").append(item.getShop().getChosenCurrency()).append(options.getPrice()).append("</b>");
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

    private void sendResultToTelegram(String chatId, String result) {
        SendMessage resMsg = new SendMessage(chatId, result);
        resMsg.enableHtml(true);
        resMsg.setDisableWebPagePreview(true);
        telegramBot.sendMessage(resMsg);
    }
}
