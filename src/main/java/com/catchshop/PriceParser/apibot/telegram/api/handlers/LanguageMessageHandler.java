package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LanguageMenuService;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class LanguageMessageHandler implements InputMessageHandler {
    private UserRepository userRepository;
    private LocaleMessageService localeMessageService;
    private LanguageMenuService languageMenuService;
    private ReplyMessageService replyMessageService;

    @Autowired
    public LanguageMessageHandler(UserRepository userRepository, LocaleMessageService localeMessageService, LanguageMenuService languageMenuService, ReplyMessageService replyMessageService) {
        this.userRepository = userRepository;
        this.localeMessageService = localeMessageService;
        this.languageMenuService = languageMenuService;
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUserInput(message);
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_LANGUAGES;
    }

    private SendMessage processUserInput(Message inputMessage) {
        String userText = inputMessage.getText();
        Long userId = inputMessage.getFrom().getId();
        String chatId = inputMessage.getChatId().toString();

//        UserRequestProfile userRequestProfile = userRepository.getUserRequestProfile(userId);
//        BotStatus botStatus = userRepository.getCurrentBotStatusFromUser(userId);

        SendMessage replyToUser = languageMenuService.getLanguageMenuMessage(chatId, localeMessageService.getMessage("reply.menu.showLanguages"));;
        return replyToUser;
    }
}
