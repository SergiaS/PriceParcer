package com.catchshop.PriceParser.apibot.telegram.util;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
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

    public void showItemFormattedResults(String chatId, ParseItem parseItem) {
        log.info("Item results: {}", parseItem);

        StringBuilder result = new StringBuilder();
        result.append("<u>").append("<a href=\"").append(parseItem.getUrl()).append("\">").append(parseItem.getTitle())
                .append("</a> [").append(parseItem.getRangePrice()).append("]</u>").append("\n");

        addOptions(parseItem, result);

        sendResultToTelegram(chatId, result.toString());
    }

    public void showShopsFormattedResults(String chatId, List<ParseItem> parseItemList) {
        log.info("Format {} shop results, Total № of items: {}", parseItemList.get(0).getShop(), parseItemList.size());

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

                if (result.length() > 3000) {
                    sendResultToTelegram(chatId, result.toString());
                    result.setLength(0);
                }
            }
        }
        sendResultToTelegram(chatId, result.toString());
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

    private void sendResultToTelegram(String chatId, String result) {
        SendMessage resMsg = new SendMessage(chatId, result);
        resMsg.enableHtml(true);
        resMsg.setDisableWebPagePreview(true);
        telegramBot.sendMessage(resMsg);
    }
}
