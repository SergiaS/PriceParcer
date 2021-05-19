package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.SearchMenuService;
import com.catchshop.PriceParser.bike.model.FavoriteItem;
import com.catchshop.PriceParser.bike.model.ShopOptions;
import com.catchshop.PriceParser.bike.shops.wiggle.WiggleParser;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public SearchMessageHandler(UserRepository userRepository, SearchMenuService searchMenuService, LocaleMessageService localeMessageService, ReplyMessageService replyMessageService) {
        this.userRepository = userRepository;
        this.searchMenuService = searchMenuService;
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    private SendMessage processUserInput(Message inputMessage) {
        String userText = inputMessage.getText();
        Long userId = inputMessage.getFrom().getId();
        Long chatId = inputMessage.getChatId();

        UserProfile userProfile = userRepository.getUserProfile(userId);
        BotStatus botStatus = userRepository.getBotStatus(userId);

        SendMessage replyToUser = searchMenuService.getSearchMenuMessage(chatId.toString(), userText);



        if (userText.equals(localeMessageService.getMessage("button.menu.showSearch"))) {
            replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.menu.showSearch");
        } else {
            // user request handler
            WiggleParser wp = new WiggleParser();
            List<FavoriteItem> favoriteItemList = wp.wiggleSearcher(userText);

            String formattedResult = wp.getFormattedResult(favoriteItemList);


//            replyToUser.setText("<b>blabla</b>");
            replyToUser.enableHtml(true);
            replyToUser.setText(formattedResult);
        }


//        if (botStatus.equals(BotStatus.SHOW_SEARCH)) {
//            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.showSearch");
//            userRepository.setBotStatus(userId, BotStatus.SHOW_SEARCH);
//        } else {
//            userRepository.saveUserProfile(userId, userProfile);
//        }


        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_SEARCH;
    }

//    private String getFormattedResult(List<FavoriteItem> itemList) {
//        if (itemList.size() == 0) {
//            return "Nothing was found";
//        }
//
//        int count = 1;
//        StringBuilder result = new StringBuilder();
//        for (FavoriteItem item : itemList) {
//            result.append(count).append(" <a href=\"").append(item.getURL()).append("\">").append(item.getItemName())
//                    .append("</a> ").append(item.getRangePrice()).append("\n");
//            count++;
//
//            for (ShopOptions options : item.getShopOptionsList()) {
//                result.append(options.getPrice()).append(", ")
//                        .append(options.getColor().isEmpty() ? "" : ", " + options.getColor())
//                        .append()
//
//
//            }
//        }
//
//        return result.toString();
//    }
}
