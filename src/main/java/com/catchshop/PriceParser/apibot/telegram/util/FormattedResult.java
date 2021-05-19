package com.catchshop.PriceParser.apibot.telegram.util;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.bike.model.FavoriteItem;
import com.catchshop.PriceParser.bike.model.ShopOptions;
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

    public static final String CURRENCY_SIGN = "$"; // €

    public void showWiggleResults(String chatId, List<FavoriteItem> itemList) {
        log.info("Format the Wiggle results, Total № of items: {}", itemList.size());

        int count = 1;
        StringBuilder result = new StringBuilder();
        for (FavoriteItem item : itemList) {
            result.append("<u>").append(count).append(" <a href=\"").append(item.getURL()).append("\">").append(item.getItemName())
                    .append("</a> [").append(item.getRangePrice()).append("]</u>").append("\n");
            count++;

            for (ShopOptions options : item.getShopOptionsList()) {
                result.append("<b>").append(CURRENCY_SIGN).append(options.getPrice()).append("</b>")
                        .append(options.getColor().isEmpty() ? "" : ", " + options.getColor())
                        .append(options.getSize().isEmpty() ? "" : ", " + options.getSize())
                        .append(", ").append(options.getStatus()).append("\n");
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
