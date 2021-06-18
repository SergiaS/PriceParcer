package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CachedParsedResult {

    private final Map<Long, ParsedItem> parsedItemMap = new HashMap<>();

    public ParsedItem getParsedItem(Long chatId) {
        return parsedItemMap.get(chatId);
    }

    public void addParsedItem(Long chatId, ParsedItem parsedItem) {
        parsedItemMap.put(chatId, parsedItem);
    }

    public void removeParsedItem(Long chatId) {
        parsedItemMap.remove(chatId);
    }

    public void saveSelectedOptions(Long chatId, ItemOptions selectedOptions) {
        ParsedItem parsedItem = getParsedItem(chatId);
        parsedItem.setSelectedOptions(selectedOptions);
        addParsedItem(chatId, parsedItem);
    }
}
