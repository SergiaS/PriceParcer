package com.catchshop.PriceParser.bike.model;

import com.catchshop.PriceParser.bike.enums.ParsedShop;

import java.util.Objects;

/**
 * Shop model is contain all setting about shop
 */
public class Shop {
    private final ParsedShop name;
    private final String url;
    private final boolean isChangeableCurrency;
    private String chosenCurrency;
    private final String originalCountry;
    private String deliveryCountry;
    private String deliveryCost; // based on deliveryCountry

    public Shop(ParsedShop name, String url, boolean isChangeableCurrency, String chosenCurrency, String originalCountry, String deliveryCountry, String deliveryCost) {
        this.name = name;
        this.url = url;
        this.isChangeableCurrency = isChangeableCurrency;
        this.chosenCurrency = chosenCurrency;
        this.originalCountry = originalCountry;
        this.deliveryCountry = deliveryCountry;
        this.deliveryCost = deliveryCost;
    }

    public ParsedShop getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isChangeableCurrency() {
        return isChangeableCurrency;
    }

    public String getChosenCurrency() {
        return chosenCurrency;
    }

    public void setChosenCurrency(String chosenCurrency) {
        this.chosenCurrency = chosenCurrency;
    }

    public String getOriginalCountry() {
        return originalCountry;
    }

    public String getDeliveryCountry() {
        return deliveryCountry;
    }

    public void setDeliveryCountry(String deliveryCountry) {
        this.deliveryCountry = deliveryCountry;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop shop = (Shop) o;
        return isChangeableCurrency == shop.isChangeableCurrency && name == shop.name && Objects.equals(url, shop.url) && Objects.equals(chosenCurrency, shop.chosenCurrency) && Objects.equals(originalCountry, shop.originalCountry) && Objects.equals(deliveryCountry, shop.deliveryCountry) && Objects.equals(deliveryCost, shop.deliveryCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, isChangeableCurrency, chosenCurrency, originalCountry, deliveryCountry, deliveryCost);
    }

    @Override
    public String toString() {
        return "Shop{" +
                "name=" + name +
                ", url='" + url + '\'' +
                ", isChangeableCurrency=" + isChangeableCurrency +
                ", chosenCurrency='" + chosenCurrency + '\'' +
                ", originalCountry='" + originalCountry + '\'' +
                ", deliveryCountry='" + deliveryCountry + '\'' +
                ", deliveryCost='" + deliveryCost +
                '}';
    }

    public static Shop getExampleShop(ParsedShop parsedShop) {
        switch (parsedShop) {
            case WIGGLE:
                return new Shop(ParsedShop.WIGGLE, "https://www.wiggle.co.uk/",true, "$","England", "Ukraine", "12.99");
            case BIKE24:
                return new Shop(ParsedShop.BIKE24, "https://www.bike24.com/",false, "???","Germany", "Ukraine", "19.99");

        }
        return null;
    }
}
