package com.catchshop.PriceParser.apibot.telegram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Works with template-files "messages texts" - messages.properties
 */
@Service
public class LocaleMessageService {
    private Locale locale;
    private final MessageSource messageSource;

    @Autowired
    public LocaleMessageService(@Value("${locale.languageTag}") String languageTag, MessageSource messageSource) {
        setLanguageTag(languageTag);
        this.messageSource = messageSource;
    }

    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }

    public void setLanguageTag(String languageTag) {
        this.locale = Locale.forLanguageTag(languageTag);
    }
}
