package com.catchshop.PriceParser.bike.shops;

import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class MainParser {

    public abstract List<ParseItem> searcher(String textToSearch);

    public abstract ParseItem parseItemInfo(String itemUrl);

    protected abstract List<ItemOptions> parseItemOptions(Document doc);

}
