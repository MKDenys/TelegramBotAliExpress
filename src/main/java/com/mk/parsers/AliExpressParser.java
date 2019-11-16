package com.mk.parsers;

import com.mk.CurrencyCode;
import com.mk.Item;
import com.mk.Price;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class AliExpressParser {

    private static final String IMG = "img";
    private static final String SRC = "src";
    private static final String CONTENT = "content";
    private static final String ITEMPROP = "itemprop";
    private static final String IMAGE_ID = "ui-image-viewer-thumb-frame";
    private static final String REGULAR_PRICE_ID = "j-sku-price";
    private static final String DISCOUNT_PRICE_ID = "j-sku-discount-price";
    private static final String PRICE_CURRENCY = "priceCurrency";
    private static final String ATTRIBUTE_ID = "id";
    private String url;
    private Document htmlDocument;


    public AliExpressParser(String url) {
        this.url = url;
        try {
            this.htmlDocument = Jsoup.connect(this.url).get();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Item parseItem() {
        return new Item(this.url, getImageUrl(), getPrice());
    }

    private Price getPrice() {
        Price price;
        String regularPrice;
        String discountPrice = "";
        CurrencyCode currencyCode;
        regularPrice = this.htmlDocument.getElementsByAttributeValue(ATTRIBUTE_ID, REGULAR_PRICE_ID).get(0).text();
        if (this.htmlDocument.getElementsByAttributeValue(ATTRIBUTE_ID, DISCOUNT_PRICE_ID).size() > 0)
            discountPrice = this.htmlDocument.getElementsByAttributeValue(ATTRIBUTE_ID, DISCOUNT_PRICE_ID).get(0).text();
        currencyCode = CurrencyCode.valueOf(this.htmlDocument
                .getElementsByAttributeValue(ITEMPROP, PRICE_CURRENCY).get(0).attr(CONTENT));
        regularPrice = regularPrice.replace("\u00A0","").replace(",", ".");
        discountPrice = discountPrice.replace("\u00A0","").replace(",", ".");
        if (regularPrice.contains(" - ")) {
            regularPrice = regularPrice.substring(0, regularPrice.indexOf(" "));
        }
        if (discountPrice.contains(" - ")) {
            discountPrice = discountPrice.substring(0, discountPrice.indexOf(" "));
        }
        if (discountPrice.isEmpty()) {
            price = new Price(Float.valueOf(regularPrice), currencyCode);
        } else {
            price = new Price(Float.valueOf(discountPrice), currencyCode);
        }
        return price;
    }

    private String getImageUrl() {
        Elements elements;
        Element imageElement;
        String urlImg = "";
        try {
            elements = this.htmlDocument.getElementsByClass(IMAGE_ID);
            imageElement = elements.get(0);
            Elements tagElements = imageElement.getElementsByTag(IMG);
            urlImg = tagElements.attr(SRC);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return urlImg;
    }
}
