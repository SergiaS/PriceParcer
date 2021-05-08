package com.catchshop.PriceParser.apibot.telegram;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class PriceParserTelegramBot extends TelegramWebhookBot {

    private String botUserName;
    private String webHookPath;
    private String botToken;

    public PriceParserTelegramBot(DefaultBotOptions options) {
        super(options);
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String chatId = message.getChatId().toString();
            String text = message.getText();
            System.out.println(" > chatId: " + chatId);
            System.out.println(" > text: " + text);

            try {
                execute(new SendMessage(chatId, "Hi, " + text));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
