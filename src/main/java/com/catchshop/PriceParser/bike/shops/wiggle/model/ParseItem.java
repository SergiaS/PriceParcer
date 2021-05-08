package com.catchshop.PriceParser.bike.shops.wiggle.model;

import java.util.List;

public class ParseItem {

    private String name;
    private String image;
    private String url;
    private String price;
    private List<PriceOptions> optionsList;

    public ParseItem(String name, String url, String price, List<PriceOptions> optionsList) {
        this.name = name;
        this.url = url;
        this.price = price;
        this.optionsList = optionsList;
    }

    public ParseItem(String name, String image, String url, String price, List<PriceOptions> optionsList) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.price = price;
        this.optionsList = optionsList;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getPrice() {
        return price;
    }

    public List<PriceOptions> getOptionsList() {
        return optionsList;
    }

    @Override
    public String toString() {
        return "ParseItem{" +
                "name='" + name + '\'' +
//                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", price='" + price + '\'' +
                ", optionsList=" + optionsList +
                '}';
    }
}
