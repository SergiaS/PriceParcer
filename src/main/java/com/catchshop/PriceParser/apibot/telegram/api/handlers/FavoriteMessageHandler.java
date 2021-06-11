package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.repository.UserRepository;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MenuKeyboardService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class FavoriteMessageHandler implements InputMessageHandler {
    private final MenuKeyboardService menuKeyboardService;
    private final UserRepository userRepository;
    private final LocaleMessageService localeMessageService;
    private final ReplyMessageService replyMessageService;
    private final ResultManager resultManager;

    @Autowired
    public FavoriteMessageHandler(MenuKeyboardService menuKeyboardService, UserRepository userRepository, LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, ResultManager resultManager) {
        this.menuKeyboardService = menuKeyboardService;
        this.userRepository = userRepository;
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.resultManager = resultManager;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        Long userId = inputMessage.getFrom().getId();
        Long chatId = inputMessage.getChatId();
        String text = inputMessage.getText();

        SendMessage replyToUser = null;
        UserProfile userProfile = userRepository.getUserProfile(userId);
        BotStatus botStatus = userRepository.getBotStatus(userId);

        List<FavoriteItem> allItems = userRepository.getAllItems(userId);
        if (botStatus.equals(BotStatus.SHOW_FAVORITES)) {
            replyToUser = allItemsAnswer(chatId, userId, allItems);
        } else if (botStatus.equals(BotStatus.SHOW_FAVORITES_DELETE)) {
            if (text.equals(localeMessageService.getMessage("button.menu.deleteFavoriteByNumber"))) {
                replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.deleteFavoriteByNumber");
            }
            // checks if entered msg contains only digits
            else if (isNumberInListRange(text, allItems)) {
                // checks if entered number is exist in the list
                int index = Integer.parseInt(text) - 1;
                if (allItems.get(index) != null) {
                    String deletedTitleItem = allItems.get(index).getTitle();
                    resultManager.sendHtmlResultToTelegram(chatId.toString(), String.format(localeMessageService.getMessage("reply.favorites.removed"), deletedTitleItem));

                    allItems.remove(index);
                    userProfile.setFavorites(allItems);
                    userRepository.saveUserProfile(userId, userProfile);

                    replyToUser = allItemsAnswer(chatId, userId, allItems);
                } else {
                    replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.removeError");
                }
            } else {
                replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.removeError");
            }
        }
        return replyToUser;
    }

    private boolean isNumberInListRange(String text, List<FavoriteItem> allItems) {
        return text.matches("\\d+") && Integer.parseInt(text) <= allItems.size();
    }

    private SendMessage allItemsAnswer(Long chatId, Long userId, List<FavoriteItem> allItems) {
        SendMessage replyToUser;
        UserProfile userProfile = userRepository.getUserProfile(userId);
        if (allItems.size() == 0) {
            userProfile.setBotStatus(BotStatus.SHOW_MENU);
            userRepository.saveUserProfile(userId, userProfile);
            replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.isEmpty");
        } else {
            if (userProfile.getBotStatus() == BotStatus.SHOW_FAVORITES_DELETE) {
                userProfile.setBotStatus(BotStatus.SHOW_FAVORITES);
                userRepository.saveUserProfile(userId, userProfile);
            }
            replyToUser = resultManager.getFavoriteItemFormattedResult(chatId.toString(), allItems);
        }
        replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
        return replyToUser;
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_FAVORITES;
    }
}
