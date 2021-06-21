package com.catchshop.PriceParser.apibot.telegram.api.handlers;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.api.InputMessageHandler;
import com.catchshop.PriceParser.apibot.telegram.model.FavoriteItem;
import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import com.catchshop.PriceParser.apibot.telegram.service.LocaleMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.MenuKeyboardService;
import com.catchshop.PriceParser.apibot.telegram.service.ReplyMessageService;
import com.catchshop.PriceParser.apibot.telegram.service.UserProfileService;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class FavoriteMessageHandler implements InputMessageHandler {
    private final MenuKeyboardService menuKeyboardService;
    private final UserProfileService userProfileService;
    private final LocaleMessageService localeMessageService;
    private final ReplyMessageService replyMessageService;
    private final ResultManager resultManager;

    @Autowired
    public FavoriteMessageHandler(MenuKeyboardService menuKeyboardService, UserProfileService userProfileService, LocaleMessageService localeMessageService, ReplyMessageService replyMessageService, ResultManager resultManager) {
        this.menuKeyboardService = menuKeyboardService;
        this.userProfileService = userProfileService;
        this.localeMessageService = localeMessageService;
        this.replyMessageService = replyMessageService;
        this.resultManager = resultManager;
    }

    @Override
    public SendMessage handle(Message inputMessage) {
        Long chatId = inputMessage.getChatId();
        String text = inputMessage.getText();

        SendMessage replyToUser = null;
        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        BotStatus botStatus = userProfile.getBotStatus();

        List<FavoriteItem> allItems = userProfile.getFavorites();
        if (botStatus.equals(BotStatus.SHOW_FAVORITES)) {
            replyToUser = getAllItems(chatId, allItems);
        } else if (botStatus.equals(BotStatus.SHOW_FAVORITES_DELETE)) {
            replyToUser = favoriteDelete(chatId, text);
        }
        return replyToUser;
    }

    private SendMessage getAllItems(Long chatId, List<FavoriteItem> allItems) {
        SendMessage replyToUser;
        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        if (allItems.size() == 0) {
            userProfile.setBotStatus(BotStatus.SHOW_MENU);
            userProfileService.saveUserProfile(userProfile);
            replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.isEmpty");
        } else {
            if (userProfile.getBotStatus() == BotStatus.SHOW_FAVORITES_DELETE) {
                userProfile.setBotStatus(BotStatus.SHOW_FAVORITES);
                userProfileService.saveUserProfile(userProfile);
            }
            replyToUser = resultManager.getFavoriteItemFormattedResult(chatId.toString(), allItems);
        }
        replyToUser.setReplyMarkup(menuKeyboardService.getMenuKeyboard(chatId));
        return replyToUser;
    }

    private SendMessage favoriteDelete(Long chatId, String userText) {
        UserProfile userProfile = userProfileService.getUserProfileData(chatId);
        SendMessage replyToUser;

        List<FavoriteItem> allItems = userProfile.getFavorites();
        if (userText.equals(localeMessageService.getMessage("button.menu.deleteFavoriteByNumber"))) {
            replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.deleteFavoriteByNumber");
        }
        // checks if entered msg contains only digits
        else if (isNumberInListRange(userText, allItems)) {
            // checks if entered number is exist in the list
            int index = Integer.parseInt(userText) - 1;
            if (allItems.get(index) != null) {
                String title = allItems.get(index).getTitle();
                String group = allItems.get(index).getOptions().getGroup();
                String color = allItems.get(index).getOptions().getColor();
                String size = allItems.get(index).getOptions().getSize();
                StringBuilder deletingItem = new StringBuilder(title)
                        .append(group == null ? "" : ", " + group)
                        .append(color == null ? "" : ", " + color)
                        .append(size == null ? "" : ", " + size);
                resultManager.sendHtmlResultToTelegram(chatId.toString(), String.format(localeMessageService.getMessage("reply.favorites.removed"), deletingItem));

                allItems.remove(index);
                userProfile.setFavorites(allItems);
                userProfileService.saveUserProfile(userProfile);

                replyToUser = getAllItems(chatId, allItems);
            } else {
                replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.removeError");
            }
        } else {
            replyToUser = replyMessageService.getReplyMessage(chatId.toString(), "reply.favorites.removeError");
        }
        return replyToUser;
    }

    private boolean isNumberInListRange(String text, List<FavoriteItem> allItems) {
        return text.matches("\\d+") && Integer.parseInt(text) <= allItems.size();
    }

    @Override
    public BotStatus getHandleName() {
        return BotStatus.SHOW_FAVORITES;
    }
}
