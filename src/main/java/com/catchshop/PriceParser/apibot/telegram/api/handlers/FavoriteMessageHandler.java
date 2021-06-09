package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.MenuKeyboardService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;

@Component
public class FavoriteMessageHandler implements InputMessageHandler {
    private final MenuKeyboardService menuKeyboardService;
    private final UserRepository userRepository;
    private final ReplyMessageService messageService;
    private final ResultManager resultManager;

    @Autowired
    public FavoriteMessageHandler(MenuKeyboardService menuKeyboardService, UserRepository userRepository, ReplyMessageService messageService, ResultManager resultManager) {
        this.menuKeyboardService = menuKeyboardService;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.resultManager = resultManager;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        Long userId = inputMessage.getFrom().getId();
        Long chatId = inputMessage.getChatId();

        ArrayDeque<FavoriteItem> allItems = userRepository.getAllItems(userId);
        SendMessage replyToUser = null;
        if (allItems.size() == 0) {
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.favorites.ifEmpty");
        } else {
            // show result
            resultManager.sendFavoriteItemFormattedResult(chatId.toString(), new ArrayList<>(allItems));

            // than ask - choose action
            // and show menu - Main menu, Parse item, Delete ...
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.showFavorites");
        }



//        UserProfile userProfile = userRepository.getUserProfile(userId);
//        BotStatus botStatus = userRepository.getBotStatus(userId);
//        if (botStatus.equals(BotStatus.SHOW_FAVORITE)) {
//            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.showFavorites");
//        } else {
//            userRepository.saveUserProfile(userId, userProfile);
//        }
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_FAVORITE;
    }
}
