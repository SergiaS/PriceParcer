package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.PriceParserTelegramBot;
import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.SearchMenuService;
import com.catchshop.PriceParser.apibot.telegram.util.FormattedResult;
import com.catchshop.PriceParser.bike.model.Item;
import com.catchshop.PriceParser.bike.shops.wiggle.WiggleParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class SearchMessageHandler implements InputMessageHandler {
    private UserRepository userRepository;
    private SearchMenuService searchMenuService;
    private LocaleMessageService localeMessageService;
    private ReplyMessageService replyMessageService;
    private PriceParserTelegramBot telegramBot;
    private FormattedResult formattedResult;

    @Autowired
    public SearchMessageHandler(UserRepository userRepository, SearchMenuService searchMenuService, LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, @Lazy PriceParserTelegramBot telegramBot, FormattedResult formattedResult) {
        this.userRepository = userRepository;
        this.searchMenuService = searchMenuService;
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.telegramBot = telegramBot;
        this.formattedResult = formattedResult;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    private SendMessage processUserInput(Message inputMessage) {
        String userText = inputMessage.getText();
//        Long userId = inputMessage.getFrom().getId();
        String chatId = inputMessage.getChatId().toString();

//        UserProfile userProfile = userRepository.getUserProfile(userId);
//        BotStatus botStatus = userRepository.getBotStatus(userId);

        SendMessage replyToUser = searchMenuService.getSearchMenuMessage(chatId, userText);

        if (userText.equals(localeMessageService.getMessage("button.menu.showSearch"))) {
            replyToUser.setText(localeMessageService.getMessage("reply.menu.showSearch"));
        } else {
            // user request handler
            telegramBot.sendMessage(replyMessageService.getReplyMessage(chatId, "reply.search.start"));

            WiggleParser wp = new WiggleParser();
            List<Item> itemList = wp.wiggleSearcher(userText);

            if (itemList.size() == 0) {
                replyToUser.setText(localeMessageService.getMessage("reply.search.notFound"));
            } else {
                formattedResult.showWiggleResults(chatId, itemList);
                replyToUser.setText(localeMessageService.getMessage("reply.search.end"));
            }
        }
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_SEARCH;
    }
}
