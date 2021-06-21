package com.catchshop.PriceParser.bike.shops.wiggle;

import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.model.Shop;
import com.catchshop.PriceParser.bike.shops.MainParser;
import com.catchshop.PriceParser.bike.util.ShopHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Can parse first 48 positions (first page) and specific position by url of Wiggle.
 * Returns sorted result - positions by price and their options by price and color.
 * <p>
 * Restricted to 25 items! Too long message (for example hit "Five Ten Freeride")
 */

public class WiggleParser extends MainParser {
    public static final String CURRENCY_TEXT = "USD"; // EUR
    private final String SITE = "https://www.wiggle.co.uk/?s=";
    private final String SORT_BY = "&o=2"; // site sort - second parameter (Price: Low to High)
    private final String IN_STOCK_ONLY = "&ris=1";
    private final String CURRENCY = "curr=" + CURRENCY_TEXT;
    private final String COUNTRY = "&prevDestCountryId=99&dest=1";

    private final Shop wiggleShop = getShop();

    public static void main(String[] args) {
        WiggleParser wp = new WiggleParser();
//        ShopHelper.printItems(wp.searcher("castelli gloves white"));

        ShopHelper.printItem(wp.parseItemInfo("https://www.wiggle.co.uk/muc-off-bike-cleaner-concentrate-500ml"));
    }

    public List<ParsedItem> searcher(String textToSearch) {
        String catalogItemsUrl = SITE +
                textToSearch.replace(" ", "+") +
                SORT_BY +
                IN_STOCK_ONLY +
                "&" + CURRENCY +
                COUNTRY;

        List<ParsedItem> itemsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(catalogItemsUrl)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .get();

            Elements items = doc
                    .selectFirst("div.MainColumn")
                    .select("div.bem-product-thumb--grid");

            for (Element item : items) {
                String itemUrl = item.select("a.bem-product-thumb__name--grid").attr("href");

                itemsList.add(parseItemInfo(itemUrl));
                if (itemsList.size() == 25) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    public ParsedItem parseItemInfo(String itemUrl) {
        itemUrl = ShopHelper.removeParameters(itemUrl);
        ParsedItem parsedItem = null;
        try {
            Document doc = Jsoup.connect(itemUrl + "?" + CURRENCY + COUNTRY).get();

            String name = doc.select("h1#productTitle").text();
            String rangePrice = doc.select("p.bem-pricing__product-price,js-unit-price").text();

            List<ItemOptions> itemOptions = parseItemOptions(doc);

            parsedItem = new ParsedItem(name, wiggleShop, itemUrl, itemOptions, rangePrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedItem;
    }

    protected List<ItemOptions> parseItemOptions(Document doc) {
        List<ItemOptions> res = new ArrayList<>();

        Elements options = doc
                .selectFirst("div.bem-sku-selector")
                .select("div.sku-items-children");

        for (Element option : options) {
            String color = option.attr("data-colour");
            if (color.isEmpty()) {
                Elements allSizesForColor = option.select("li.bem-sku-selector__option-group-item");
                for (Element element : allSizesForColor) {
                    String group = element.select("span.bem-sku-selector__size").text();
                    String price = element.select("span.bem-sku-selector__price").text()
                            .replace(wiggleShop.getChosenCurrency(), "")
                            .replace(",", "");
                    String status = element.select("span.bem-sku-selector__status-stock").text();

                    // change original status
                    if (status.equals("Out of stock. Let me know when in stock.")) {
                        status = "Out of stock";
                    } else if (status.isEmpty()) {
                        status = "In stock";
                    }
                    res.add(new ItemOptions(group, new BigDecimal(price), status));
                }
            } else if (!color.equals("null")) {
                Elements allSizesForColor = option.select("li.bem-sku-selector__option-group-item");
                for (Element element : allSizesForColor) {
                    String size = element.select("span.bem-sku-selector__size").text();
                    String price = element.select("span.bem-sku-selector__price").text()
                            .replace(wiggleShop.getChosenCurrency(), "")
                            .replace(",", "");
                    String status = element.select("span.bem-sku-selector__status-stock").text();

                    // change original status
                    if (status.equals("Out of stock. Let me know when in stock.")) {
                        status = "Out of stock";
                    } else if (status.isEmpty()) {
                        status = "In stock";
                    }
                    res.add(new ItemOptions(color, size, new BigDecimal(price), status));
                }
            }
        }
        if (res.size() > 1) {
            res.sort(Comparator.comparing(ItemOptions::getPrice)
                    .thenComparing(ItemOptions::getColor));
        }
        return res;
    }

    private Shop getShop() {
        return new Shop(
                ParsedShop.WIGGLE,
                "wiggle.co.uk",
                true,
                "$",
                "United Kingdom",
                "United Kingdom",
                "n/a");
    }

}
