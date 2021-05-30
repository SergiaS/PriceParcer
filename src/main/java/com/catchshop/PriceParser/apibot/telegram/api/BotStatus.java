package com.catchshop.PriceParser.apibot.telegram.api;

/**
 * The states/statuses of the bot
 */
public enum BotStatus {
    SHOW_MENU,
    SHOW_SEARCH,
    SHOW_FAVORITE,
    SHOW_LANGUAGES,
    SHOW_ERROR,

    SHOW_PARSE,
    SHOW_PARSE_END,
    ASK_COLOR,
    ASK_SIZE,
    ASK_GROUP,

    SHOW_HELP
}
