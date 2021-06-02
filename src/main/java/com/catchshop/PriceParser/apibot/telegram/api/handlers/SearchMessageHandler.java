package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MenuKeyboardService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.util.FormattedResult;
import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.bike.model.Item;
import com.catchshop.PriceParser.bike.shops.bike24.Bike24Parser;
import com.catchshop.PriceParser.bike.shops.wiggle.WiggleParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class SearchMessageHandler implements InputMessageHandler {
    private final MenuKeyboardService menuKeyboardService;
    private final LocaleMessageService localeMessageService;
    private final ReplyMessageService replyMessageService;
    private final PriceParserTelegramBot telegramBot;
    private final FormattedResult formattedResult;

    @Autowired
    public SearchMessageHandler(MenuKeyboardService menuKeyboardService, LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, @Lazy PriceParserTelegramBot telegramBot, FormattedResult formattedResult) {
        this.menuKeyboardService = menuKeyboardService;
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.telegramBot = telegramBot;
        this.formattedResult = formattedResult;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        String userText = inputMessage.getText();
        String chatId = inputMessage.getChatId().toString();

        SendMessage replyToUser = menuKeyboardService.getMenuMessage(chatId, userText);

        if (userText.equals(localeMessageService.getMessage("button.menu.showSearch"))) {
            replyToUser.setText(localeMessageService.getMessage("reply.menu.showSearch"));
        } else {
            // user request handler
            telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId, "reply.search.start"));

            WiggleParser wp = new WiggleParser();
            List<Item> wiggleItemsList = wp.wiggleSearcher(userText);
            messageIfNotFound(chatId, wiggleItemsList, ParsedShop.WIGGLE.name());

            Bike24Parser b24p = new Bike24Parser();
            List<Item> bike24ItemsList = b24p.bike24Searcher(userText);
            messageIfNotFound(chatId, bike24ItemsList, ParsedShop.BIKE24.name());

            if (!wiggleItemsList.isEmpty() || !bike24ItemsList.isEmpty()) {
                replyToUser.setText(localeMessageService.getMessage("reply.search.goodEnd"));
            } else {
                replyToUser.setText(localeMessageService.getMessage("reply.search.badEnd"));
            }
        }
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_SEARCH;
    }

    private void messageIfNotFound(String chatId, List<Item> items, String shopName) {
        if (items.size() == 0) {
            telegramBot.sendMessage(new SendMessage(chatId, String.format(localeMessageService.getMessage("reply.notFound"), shopName)));
        } else {
            telegramBot.sendMessage(new SendMessage(chatId, String.format(localeMessageService.getMessage("reply.search.results"), shopName)));
            formattedResult.showShopsFormattedResults(chatId, items);
        }
    }
}
