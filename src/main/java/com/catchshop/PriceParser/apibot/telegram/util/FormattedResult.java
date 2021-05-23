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

    private PriceParserTelegramBot telegramBot;

    @Autowired
    public FormattedResult(@Lazy PriceParserTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void showWiggleResults(String chatId, List<Item> itemList, final String CURRENCY_SIGN) {
        log.info("Format some shop results, Total № of items: {}", itemList.size());

        int count = 1;
        StringBuilder result = new StringBuilder();
        for (Item item : itemList) {
            result.append("<u>").append(count).append(" <a href=\"").append(item.getURL()).append("\">").append(item.getTitle())
                    .append("</a> [").append(item.getRangePrice()).append("]</u>").append("\n");
            count++;

            for (ItemOptions options : item.getItemOptionsList()) {
                result.append("<b>").append(CURRENCY_SIGN).append(options.getPrice()).append("</b>");
                if (options.getColor() == null) {
                    result
                        .append(options.getGroup().isEmpty() ? "" : ", " + options.getGroup())
                        .append(options.getStatus().isEmpty() ? "" : ", " + options.getStatus());
                } else {
                    result
                        .append(options.getColor().isEmpty() ? "" : ", " + options.getColor())
                        .append(options.getSize().isEmpty() ? "" : ", " + options.getSize())
                        .append(options.getStatus().isEmpty() ? "" : ", " + options.getStatus());
                }
                result.append("\n");
            }
            if (result.length() > 3000) {
                sendResultToTelegram(chatId, result.toString());
                result.setLength(0);
            }
        }
        sendResultToTelegram(chatId, result.toString());
    }

    private void sendResultToTelegram(String chatId, String result) {
        SendMessage resMsg = new SendMessage(chatId, result);
        resMsg.enableHtml(true);
        resMsg.setDisableWebPagePreview(true);
        telegramBot.sendMessage(resMsg);
    }
}
