package com.catchshop.PriceParser.apibot.telegram.api;

/**
 * The states/statuses of the bot
 */
public enum BotStatus {
    SHOW_MENU,
    SHOW_SEARCH,
    SHOW_FAVORITES,
    SHOW_FAVORITES_DELETE,
    SHOW_LANGUAGES,
    SHOW_ERROR,

    SHOW_PARSE,
    SHOW_PARSE_END,
    FILLING_ITEM,
    ASK_TRACKING,

    SHOW_HELP
}
