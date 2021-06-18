package com.catchshop.PriceParser.bike.shops;

import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class MainParser {

    public abstract List<ParsedItem> searcher(String textToSearch);

    public abstract ParsedItem parseItemInfo(String itemUrl);

    protected abstract List<ItemOptions> parseItemOptions(Document doc);

}
