package com.mk;

public class Item {
    private String name;
    private String url;
    private String imageUrl;
    private Price price;

    public Item (String url, String imageUrl, Price price) {
        this.url = url;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Price getPrice() {
        return price;
    }
}
