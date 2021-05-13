package com.catchshop.PriceParser.apibot.telegram.api;

/**
 * The states/statuses of the bot
 */
public enum BotStatus {
    MAIN_MENU,
    SHOW_FAVORITE,
    ADD_TO_FAVORITE,
    LANGUAGE_SETTINGS,

    SEARCH_MENU,
    SEARCH_PROCESS,

    SHOW_RESULT,
    SHOW_HELP_MENU
}
