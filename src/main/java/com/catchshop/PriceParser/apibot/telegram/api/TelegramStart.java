package com.catchshop.PriceParser.apibot.telegram.api;

import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class TelegramStart {
    private BotStatusContext botStatusContext;
    private UserRepository userRepository;
    private LocaleMessageService messageService;

    @Autowired
    public TelegramStart(BotStatusContext botStatusContext, UserRepository userRepository, LocaleMessageService messageService) {
        this.botStatusContext = botStatusContext;
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User: {}, chatId: {}, with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMessage = message.getText();
        Long userId = message.getFrom().getId();
        BotStatus botStatus;

        if (inputMessage.equals(messageService.getMessage("button.menu.mainMenu"))) {
            botStatus = BotStatus.MAIN_MENU;
        } else if (inputMessage.equals(messageService.getMessage("button.menu.favorite"))) {
            botStatus = BotStatus.SHOW_FAVORITE;
        } else if (inputMessage.equals(messageService.getMessage("button.menu.addToFavorite"))) {
            botStatus = BotStatus.ADD_TO_FAVORITE;
        } else if (inputMessage.equals(messageService.getMessage("button.menu.changeLanguage"))) {
            botStatus = BotStatus.LANGUAGE_SETTINGS;

        } else if (inputMessage.equals(messageService.getMessage("button.search.searchMenu"))) {
            botStatus = BotStatus.SEARCH_MENU;
        } else if (inputMessage.equals(messageService.getMessage("button.search.searchProcess"))) {
            botStatus = BotStatus.SEARCH_PROCESS;
        } else {
            botStatus = BotStatus.MAIN_MENU;
//            botStatus = userRepository.getCurrentBotStatusFromUser(userId);
        }

        userRepository.setCurrentBotStatusToUser(userId, botStatus);

        return botStatusContext.processInputMessage(botStatus, message);
    }
}
