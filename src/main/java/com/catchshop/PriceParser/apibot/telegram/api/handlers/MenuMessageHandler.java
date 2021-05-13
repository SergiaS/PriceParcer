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
public class MenuMessageHandler implements InputMessageHandler {
    private UserRepository userRepository;
    private ReplyMessageService messageService;

    @Autowired
    public MenuMessageHandler(UserRepository userRepository, ReplyMessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    private SendMessage processUserInput(Message inputMessage) {

        Long userId = inputMessage.getFrom().getId();
        Long chatId = inputMessage.getChatId();

        UserRequestProfile userRequestProfile = userRepository.getUserRequestProfile(userId);
        BotStatus botStatus = userRepository.getCurrentBotStatusFromUser(userId);

        SendMessage replyToUser = null;

        if (botStatus.equals(BotStatus.MAIN_MENU)) {
            userRepository.setCurrentBotStatusToUser(userId, BotStatus.MAIN_MENU);
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.mainMenu");
        } else if (botStatus.equals(BotStatus.SHOW_FAVORITE)) {
            userRepository.setCurrentBotStatusToUser(userId, BotStatus.SHOW_FAVORITE);
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.favorite");
        } else if (botStatus.equals(BotStatus.ADD_TO_FAVORITE)) {
            userRepository.setCurrentBotStatusToUser(userId, BotStatus.ADD_TO_FAVORITE);
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.addToFavorite");
            String userAnswer = inputMessage.getText();
            String formattedText = String.format(replyToUser.getText(), userAnswer);
            replyToUser.setText(formattedText);
        } else if (botStatus.equals(BotStatus.LANGUAGE_SETTINGS)) {
            userRepository.setCurrentBotStatusToUser(userId, BotStatus.LANGUAGE_SETTINGS);
            replyToUser = messageService.getReplyMessage(chatId.toString(), "reply.menu.changeLanguage");
            String userAnswer = inputMessage.getText();
            String formattedText = String.format(replyToUser.getText(), userAnswer);
            replyToUser.setText(String.format(replyToUser.getText(), formattedText));
        }

        userRepository.saveUserRequestProfile(userId, userRequestProfile);

        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.MAIN_MENU;
    }
}
