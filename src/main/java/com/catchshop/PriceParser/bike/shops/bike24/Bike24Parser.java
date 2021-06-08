package com.catchshop.PriceParser.bike.shops.bike24;

import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.apibot.telegram.model.ParseItem;
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Can parse first 30 positions (first page) and specific position by url of Bike24.
 * Returns sorted result - positions and their options by price.
 */
public class Bike24Parser extends MainParser {
    private final String SITE = "https://www.bike24.com";
    private final String SEARCH = "/search?searchTerm=";
    private final String SORT_BY = "&sort=price_asc";

    private final Shop bike24Shop = getShop();

    public static void main(String[] args) {
        Bike24Parser b24 = new Bike24Parser();
//        ShopHelper.printItems(b24.bike24Searcher("giro syntax"));

        ShopHelper.printItem(b24.parseItemInfo("https://www.bike24.com/p2382639.html"));
    }

    public List<ParseItem> searcher(String textToSearch) {
        String catalogUrl = SITE + SEARCH + textToSearch + SORT_BY;

        List<ParseItem> itemsList = new ArrayList<>();
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

    public ParseItem parseItemInfo(String itemUrl) {
        ShopHelper.allowAllCertificates(); // very important!

        ParseItem parseItem = null;
        try {
            Document doc = Jsoup.connect(itemUrl).get();

            String name = ShopHelper.cleanTitle(doc.select("h1").text());
            String rangePrice;

            boolean isPriceFrom = doc.select("span.js-price-from").text().contains("from");
            if (isPriceFrom) {
                rangePrice = "from " + bike24Shop.getChosenCurrency() + doc.select("span.text-value,js-price-value").attr("content");
            } else {
                rangePrice = bike24Shop.getChosenCurrency() + doc.select("span.text-value,js-price-value").attr("content");
            }

            List<ItemOptions> itemOptionsList = parseItemOptions(doc);

            parseItem = new ParseItem(name, bike24Shop, itemUrl, itemOptionsList, rangePrice);
            parseItem.setOptions(new ItemOptions(null, BigDecimal.ZERO, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseItem;
    }


    protected List<ItemOptions> parseItemOptions(Document doc) {
        List<ItemOptions> res = new ArrayList<>();

        BigDecimal basePrice = new BigDecimal(doc.select("span.text-value,js-price-value")
                .attr("content"));

//         Elements optionsMenu = doc.select("select.js-product-option-select").select("option:contains( )");
        Elements optionsMenu = doc.select("select.js-product-option-select")
                .select("option:matches([^-])");

        if (optionsMenu.isEmpty()) {
            String status = doc.select("span.js-current-stock-label,text-av-green").text();
            status = ShopHelper.returnNullIfEmpty(status);
            res.add(new ItemOptions(null, basePrice, status));
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

    private Shop getShop() {
        return new Shop(
                ParsedShop.BIKE24,
                "bike24.com",
                false,
                "€",
                "Germany",
                "Germany",
                "n/a");
    }

    // will remove info like " - add 0,84 €" and "head circumference"
    private static String cleanGroupValue(String group) {
        return group.replace(" head circumference", "")
                .replace("not deliverable: ", "")
                .replaceAll("\\s-\\s(add)\\s[\\d]*,[\\d]*\\s€", "");
    }

}
