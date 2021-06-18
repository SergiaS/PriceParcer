package com.catchshop.PriceParser.bike.util;

import com.catchshop.PriceParser.apibot.telegram.model.ParsedItem;
import com.catchshop.PriceParser.bike.model.ItemOptions;
import com.catchshop.PriceParser.bike.shops.MainParser;
import com.catchshop.PriceParser.bike.shops.bike24.Bike24Parser;
import com.catchshop.PriceParser.bike.shops.wiggle.WiggleParser;

import javax.net.ssl.*;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class ShopHelper {

    private static int itemCount = 1;

    public static void allowAllCertificates() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        try {
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static void printItem(final ParsedItem parsedItem) {
        String title = parsedItem.getTitle();
        String rangePrice = parsedItem.getRangePrice();
        String url = parsedItem.getUrl();
        String currency = parsedItem.getShop().getChosenCurrency();

        System.out.println("No." + (itemCount++) + " - " + title + " [" + rangePrice + "]");
        System.out.println("URL: " + url);

        List<ItemOptions> shopOptionsList = parsedItem.getParsedOptionsList();
        printShopOptions(shopOptionsList, currency);
    }

    public static void printItems(List<ParsedItem> itemsList) {
        for (int i = 0; i < itemsList.size(); i++) {
            String title = itemsList.get(i).getTitle();
            String priceRange = itemsList.get(i).getRangePrice();
            String url = itemsList.get(i).getUrl();
            String currency = itemsList.get(i).getShop().getChosenCurrency();

            System.out.println("No." + (i + 1) + " - " + title + " [" + priceRange + "]");
            System.out.println("URL: " + url);

            List<ItemOptions> optionsList = itemsList.get(i).getParsedOptionsList();
            printShopOptions(optionsList, currency);
        }
    }

    private static void printShopOptions(List<ItemOptions> optionsList, String currency) {
        for (ItemOptions options : optionsList) {
            if (options.getColor() == null) {
                String group = options.getGroup();
                BigDecimal price = options.getPrice();
                String status = options.getStatus();

                System.out.println(currency + price +
                        (group == null || group.isBlank() ? "" : ", " + group) +
                        (status == null || status.isBlank() ? "" : ", " + status));
            } else {
                String size = options.getSize();
                String color = options.getColor();
                BigDecimal price = options.getPrice();
                String status = options.getStatus();
                System.out.println(currency + price +
                        (color == null || color.isBlank() ? "" : ", " + color) +
                        (size == null || size.isBlank() ? "" : ", " + size) +
                        (status == null || status.isBlank() ? "" : ", [" + status + "]"));
            }
        }
        System.out.println();
    }

    public static String cleanTitle(String inputText) {
        return inputText
                .replace("É","E")
                .replace("é","e")
                .replace("®", "")
                .replace("™", "")
            ;
    }

    public static String returnNullIfEmpty(String text) {
        return text.equals("") ? null : text;
    }

    public static MainParser storeIdentifier(String itemUrl) {
        if (itemUrl.contains("wiggle")) {
            return new WiggleParser();
        }
        return new Bike24Parser();
    }

    public static String removeParameters(String url) {
        int index = url.indexOf("?");
        if (index > 0) {
            return url.substring(0, index);
        }
        return url;
    }

}
