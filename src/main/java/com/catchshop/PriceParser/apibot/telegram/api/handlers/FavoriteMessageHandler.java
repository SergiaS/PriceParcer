package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class FavoriteMessageHandler implements InputMessageHandler {
    private UserRepository userRepository;
    private ReplyMessageService messageService;

    @Autowired
    public FavoriteMessageHandler(UserRepository userRepository, ReplyMessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    private SendMessage processUserInput(Message inputMessage) {
//        String userAnswer = inputMessage.getText();
        Long userId = inputMessage.getFrom().getId();
        Long chatId = inputMessage.getChatId();

        UserProfile userProfile = userRepository.getUserProfile(userId);
        BotStatus botStatus = userRepository.getBotStatus(userId);

        SendMessage replyToUser = null;
        if (botStatus.equals(BotStatus.SHOW_FAVORITE)) {
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.showFavorite");
            userRepository.setBotStatus(userId, BotStatus.SHOW_FAVORITE);
        } else {
            userRepository.saveUserProfile(userId, userProfile);
        }
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_FAVORITE;
    }
}
