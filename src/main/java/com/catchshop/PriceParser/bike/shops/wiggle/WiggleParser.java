package com.catchshop.PriceParser.bike.shops.wiggle;

import com.catchshop.PriceParser.bike.enums.ParsedShop;
import com.catchshop.PriceParser.bike.model.FavoriteItem;
import com.catchshop.PriceParser.bike.model.ShopOptions;
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
    public final String SITE = "https://www.wiggle.co.uk/?s=";
    public final String SORT_BY = "&o=2"; // site sort - second parameter (Price: Low to High)
    public final String IN_STOCK_ONLY = "&ris=1";
    public final String CURRENCY = "curr=" + CURRENCY_TEXT;
    public final String COUNTRY = "&prevDestCountryId=99&dest=1";

    public static void main(String[] args) {
        WiggleParser wp = new WiggleParser();
        printResponse(wp.wiggleSearcher("castelli gloves white"));
    }

    public List<FavoriteItem> wiggleSearcher(String textToSearch) {
        String catalogItemsUrl = SITE +
                textToSearch.replace(" ", "+") +
                SORT_BY +
                IN_STOCK_ONLY +
                "&" + CURRENCY +
                COUNTRY;

        return searchByText(catalogItemsUrl);
    }

    private List<FavoriteItem> searchByText(String catalogItemsUrl) {
        List<FavoriteItem> favoriteItemsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(catalogItemsUrl)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .get();

            Elements items = doc
                    .selectFirst("div.MainColumn")
                    .select("div.bem-product-thumb--grid");

            for (Element item : items) {
                String name = item.select("a.bem-product-thumb__name--grid").text();
//                String image = item.getElementsByTag("img").attr("src");
                String itemUrlFromCatalog = item.select("a.bem-product-thumb__name--grid").attr("href");
                String rangePrice = item.select("span.bem-product-price__unit--grid").text();

                List<ShopOptions> itemShopOptions = findShopOptions(itemUrlFromCatalog);

                if (favoriteItemsList.size() == 10) {
                    break;
                } else {
                    favoriteItemsList.add(new FavoriteItem(name, ParsedShop.WIGGLE, itemUrlFromCatalog, itemShopOptions, rangePrice));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return favoriteItemsList;
    }

    private List<ShopOptions> findShopOptions(String urlToSearch) {
        String itemUrl = urlToSearch + "?" + CURRENCY + COUNTRY;
        List<ShopOptions> res = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(itemUrl).get();
            Elements options = doc
                    .selectFirst("div.bem-sku-selector")
                    .select("div.sku-items-children");

            for (Element option : options) {
                String color = option.attr("data-colour");
                if (!color.equals("null")) {
                    Elements allSizesForColor = option.select("li.bem-sku-selector__option-group-item");

                    for (Element element : allSizesForColor) {
                        String size = element.select("span.bem-sku-selector__size").text();
                        String price = element.select("span.bem-sku-selector__price").text()
                                .replace(CURRENCY_SIGN, "")
                                .replace(",", "");
                        String status = element.select("span.bem-sku-selector__status-stock").text();

                        // change original status
                        if (status.equals("Out of stock. Let me know when in stock.")) {
                            status = "Out of stock";
                        } else if (status.isEmpty()) {
                            status = "In stock";
                        }
                        res.add(new ShopOptions(color, size, new BigDecimal(price), status));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.sort(Comparator.comparing(ShopOptions::getPrice)
                .thenComparing(ShopOptions::getColor));

        return res;
    }

//    public String getFormattedResult(List<FavoriteItem> itemList) {
//        if (itemList.size() == 0) {
//            return "Nothing was found";
//        }
//
//        int count = 1;
//        StringBuilder result = new StringBuilder();
//        for (FavoriteItem item : itemList) {
//            result.append("<u>").append(count).append(" <a href=\"").append(item.getURL()).append("\">").append(item.getItemName())
//                    .append("</a> ").append(item.getRangePrice()).append("</u>").append("\n");
//            count++;
//
//            for (ShopOptions options : item.getShopOptionsList()) {
//                result.append("<b>").append(CURRENCY_SIGN).append(options.getPrice()).append("</b>")
//                        .append(options.getColor().isEmpty() ? "" : ", " + options.getColor())
//                        .append(options.getSize().isEmpty() ? "" : ", " + options.getSize())
//                        .append(", ").append(options.getStatus()).append("\n");
//            }
//        }
//        return result.toString();
//    }

    private static void printResponse(List<FavoriteItem> itemsList) {
        for (int i = 0; i < itemsList.size(); i++) {
            String name = itemsList.get(i).getItemName();
            String priceRange = itemsList.get(i).getRangePrice();
            String url = itemsList.get(i).getURL();

            System.out.println("No." + (i + 1) + " - " + name + " [" + priceRange + "]");
            System.out.println("URL: " + url);

            List<ShopOptions> optionsList = itemsList.get(i).getShopOptionsList();
            for (ShopOptions options : optionsList) {
                String size = options.getSize();
                String color = options.getColor();
                BigDecimal price = options.getPrice();
                String status = options.getStatus();

                System.out.println(CURRENCY_SIGN + price +
                        (color.isEmpty() ? "" : " / " + color) +
                        (size.isEmpty() ? "" : " / " + size) +
                        " / [" + status + "]");
            }
            System.out.println();
        }
    }
}
