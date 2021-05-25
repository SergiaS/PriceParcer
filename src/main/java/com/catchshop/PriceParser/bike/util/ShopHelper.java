package com.catchshop.PriceParser.bike.util;

import com.catchshop.PriceParser.bike.model.Item;
import com.catchshop.PriceParser.bike.model.ItemOptions;

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

    public static void printItem(final Item item) {
        String title = item.getTitle();
        String rangePrice = item.getRangePrice();
        String url = item.getURL();
        String currency = item.getShop().getChosenCurrency();

        System.out.println("No." + (itemCount++) + " - " + title + " [" + rangePrice + "]");
        System.out.println("URL: " + url);

        List<ItemOptions> shopOptionsList = item.getItemOptionsList();
        printShopOptions(shopOptionsList, currency);
    }

    public static void printItems(List<Item> itemsList) {
        for (int i = 0; i < itemsList.size(); i++) {
            String title = itemsList.get(i).getTitle();
            String priceRange = itemsList.get(i).getRangePrice();
            String url = itemsList.get(i).getURL();
            String currency = itemsList.get(i).getShop().getChosenCurrency();

            System.out.println("No." + (i + 1) + " - " + title + " [" + priceRange + "]");
            System.out.println("URL: " + url);

            List<ItemOptions> optionsList = itemsList.get(i).getItemOptionsList();
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
                        (group.isEmpty() ? "" : ", " + group) +
                        (status.isEmpty() ? "" : ", " + status));
            } else {
                String size = options.getSize();
                String color = options.getColor();
                BigDecimal price = options.getPrice();
                String status = options.getStatus();
                System.out.println(currency + price +
                        (color.isEmpty() ? "" : ", " + color) +
                        (size.isEmpty() ? "" : ", " + size) +
                        (status.isEmpty() ? "" : ", [" + status + "]"));
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
}
