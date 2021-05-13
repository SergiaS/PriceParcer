package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.UserRequestProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class OtherMessageHandler implements InputMessageHandler {
    private UserRepository userRepository;
    private ReplyMessageService messageService;

    @Autowired
    public OtherMessageHandler(UserRepository userRepository, ReplyMessageService messageService) {
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

        UserRequestProfile userRequestProfile = userRepository.getUserRequestProfile(userId);
        BotStatus botStatus = userRepository.getCurrentBotStatusFromUser(userId);

        SendMessage replyToUser = null;

        if (botStatus.equals(BotStatus.SEARCH_MENU)) {
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.search.searchMenu");
            userRepository.setCurrentBotStatusToUser(userId, BotStatus.SEARCH_MENU);
        } else if (botStatus.equals(BotStatus.SEARCH_PROCESS)) {
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.search.searchProcess");
            userRepository.setCurrentBotStatusToUser(userId, BotStatus.SEARCH_PROCESS);
        }

        userRepository.saveUserRequestProfile(userId, userRequestProfile);

        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SEARCH_MENU;
    }
}
