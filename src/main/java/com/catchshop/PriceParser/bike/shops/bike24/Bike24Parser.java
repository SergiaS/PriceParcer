package com.catchshop.PriceParser.bike.shops.bike24;

import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.bike.model.Item;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.util.ShopHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Can parse first 30 positions (first page) and specific position by url of Bike24.
 * Returns sorted result - positions and their options by price.
 */
public class Bike24Parser {
    public static final String CURRENCY_TEXT = "EUR";
    public static final String CURRENCY_SIGN = "€";
    private final String SITE = "https://www.bike24.com";
    private final String SEARCH = "/search?searchTerm=";
    private final String SORT_BY = "&sort=price_asc";

    public static void main(String[] args) {
        Bike24Parser b24 = new Bike24Parser();
        ShopHelper.printItems(b24.bike24Searcher("giro syntax"), CURRENCY_SIGN);

//        ShopHelper.printItem(b24.parseItemInfo("https://www.bike24.com/p2276744.html"), CURRENCY_SIGN);
    }

    public List<Item> bike24Searcher(String textToSearch) {
        String catalogUrl = SITE + SEARCH + textToSearch + SORT_BY;

        List<Item> itemsList = new ArrayList<>();
        ShopHelper.allowAllCertificates(); // very important!
        try {
            Document doc = Jsoup.connect(catalogUrl)
                    .timeout(10000)
                    .get();

            Elements items = doc
                    .selectFirst("main")
                    .select("div.col-xs-9")
                    .select("div.col-md-6");

            for (Element item : items) {
                String itemUrl = SITE + item.select("a.js-gtm-push-event-with-callback").attr("href");

                itemsList.add(parseItemInfo(itemUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    private Item parseItemInfo(String itemUrl) {
        ShopHelper.allowAllCertificates(); // very important!

        Item item = null;
        try {
            Document doc = Jsoup.connect(itemUrl).get();

            String name = ShopHelper.cleanTitle(doc.select("h1").text());
            String rangePrice;

            boolean isPriceFrom = doc.select("span.js-price-from").text().contains("from");
            if (isPriceFrom) {
                rangePrice = "from " + CURRENCY_SIGN + doc.select("span.text-value,js-price-value").attr("content");
            } else {
                rangePrice = CURRENCY_SIGN + doc.select("span.text-value,js-price-value").attr("content");
            }

            List<ItemOptions> itemOptionsList = parseItemOptions(doc);

            item = new Item(name, ParsedShop.BIKE24, itemUrl, itemOptionsList, rangePrice);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }


    private List<ItemOptions> parseItemOptions(Document doc) {
        List<ItemOptions> res = new ArrayList<>();

        BigDecimal basePrice = new BigDecimal(doc.select("span.text-value,js-price-value")
                .attr("content"));

//         Elements optionsMenu = doc.select("select.js-product-option-select").select("option:contains( )");
        Elements optionsMenu = doc.select("select.js-product-option-select")
                .select("option:matches([^-])");

        if (optionsMenu.isEmpty()) {
            String status = doc.select("span.js-current-stock-label,text-av-green").text();
            res.add(new ItemOptions("", basePrice, status));
        } else {
            Elements optionsList = doc.select("table.table-availability")
                    .select("tbody")
                    .select("tr");

            for (int i = 0; i < optionsList.size(); i++) {
                String group = optionsMenu.get(i).text();
                String status = optionsList.get(i).select("td").get(0).text();
                BigDecimal itemPrice = !group.contains("add") ? basePrice :
                        basePrice.add(new BigDecimal(optionsMenu.get(i).attr("data-surcharge"))).setScale(2, RoundingMode.FLOOR);

                res.add(new ItemOptions(cleanGroupValue(group), itemPrice, status));
            }
        }
        res.sort(Comparator.comparing(ItemOptions::getPrice));

        return res;
    }

    // will remove info like " - add 0,84 €" and "head circumference"
    private static String cleanGroupValue(String group) {
        return group.replace(" head circumference", "")
                .replace("not deliverable: Ø ", "")
                .replaceAll("\\s-\\s(add)\\s[\\d]*,[\\d]*\\s€", "");
    }
}
