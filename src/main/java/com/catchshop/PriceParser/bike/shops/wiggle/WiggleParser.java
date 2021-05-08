package com.catchshop.PriceParser.bike.shops.wiggle;

import com.catchshop.PriceParser.bike.shops.wiggle.model.ParseItem;
import com.catchshop.PriceParser.bike.shops.wiggle.model.PriceOptions;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WiggleParser {

    public static final String CURRENCY_TEXT = "USD"; // EUR
    public static final String CURRENCY_SIGN = "$"; // €

    public static void main(String[] args) {
        WiggleParser wp = new WiggleParser();
        wp.search("lezyne");
    }

    private void search(String whatToSearch) {
        final String site = "https://www.wiggle.co.uk/?s=";
        final String sortBy = "&o=2"; // site sort - second parameter (Price: Low to High)
        final String inStockOnly = "&ris=1";

        final String urlCatalogItems = site + whatToSearch.replace(" ", "+") +
                sortBy + inStockOnly + "&curr=" + CURRENCY_TEXT + "&prevDestCountryId=99&dest=1";

        printResponse(searchByRequestString(urlCatalogItems));
    }

    private List<ParseItem> searchByRequestString(final String urlCatalogItems) {
        List<ParseItem> parseItemList = new ArrayList<>();

        System.out.println("URL PARSE: " + urlCatalogItems + "\n");
        try {
            Connection connection = Jsoup.connect(urlCatalogItems)
                    .method(Connection.Method.GET)
                    .followRedirects(true);

            Document doc = connection
                    .get();
            Elements items = doc
                    .selectFirst("div.MainColumn")
                    .select("div.bem-product-thumb--grid");

            // iterate by each item
            for (Element item : items) {
                String name = item.select("a.bem-product-thumb__name--grid").text();
//                String image = item.getElementsByTag("img").attr("src");
                String url = item.select("a.bem-product-thumb__name--grid").attr("href");
                String price = item.select("span.bem-product-price__unit--grid").text();

                parseItemList.add(new ParseItem(name, url, price, getPriceOptions(url)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseItemList;
    }

    private List<PriceOptions> getPriceOptions(String url) {
        List<PriceOptions> res = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url + "?curr=" + CURRENCY_TEXT + "&prevDestCountryId=99&dest=1")
                    .get();
            Elements options = doc
                    .selectFirst("div.bem-sku-selector")
                    .select("div.sku-items-children");

            for (Element option : options) {
                String color = option.attr("data-colour");
                if (!color.equals("null")) {
                    Elements allSizesForColor = option.select("li.bem-sku-selector__option-group-item");

                    for (Element e : allSizesForColor) {
                        String size = e.select("span.bem-sku-selector__size").text();
                        String price = e.select("span.bem-sku-selector__price").text()
                                .replace(CURRENCY_SIGN, "")
                                .replace(",", "");
                        String status = e.select("span.bem-sku-selector__status-stock").text();
                        res.add(new PriceOptions(size, color, new BigDecimal(price), status));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.sort(Comparator.comparing(PriceOptions::getPrice)
                .thenComparing(PriceOptions::getColor));
        return res;
    }

    private static void printResponse(List<ParseItem> itemsList) {
        for (int i = 0; i < itemsList.size(); i++) {
            String name = itemsList.get(i).getName();
            String priceRange = itemsList.get(i).getPrice();
            String url = itemsList.get(i).getUrl();

            System.out.println("No." + (i + 1) + " - " + name + " [" + priceRange + "]");
            System.out.println("URL: " + url);

            List<PriceOptions> optionsList = itemsList.get(i).getOptionsList();
            for (PriceOptions priceOptions : optionsList) {
                String size = priceOptions.getSize();
                String color = priceOptions.getColor();
                BigDecimal price = priceOptions.getPrice();
                String status = priceOptions.getStatus();

                System.out.println(CURRENCY_SIGN + price +
                        (color.isEmpty() ? "" : " / " + color) +
                        (size.isEmpty() ? "" : " / " + size) +
                        " / [" + (status.isEmpty() ? "In stock" : status) + "]");
            }
            System.out.println();
        }
    }
}
