package com.mk;

import com.mk.parsers.CurrencyParser;

import java.text.DecimalFormat;

public class Price {

    private static final String USD = "$";
    private static final String RUB = "руб.";
    private static final String UAH = "грн.";
    private static final String PRICE_FORMAT = "0.00";
    private double priseUSD;
    private double priseRUB;
    private double priseUAH;

    public Price(double prise, CurrencyCode currencyCode) {
        CurrencyParser currencyParser = new CurrencyParser();
        switch (currencyCode) {
            case USD:
                this.priseUSD = prise;
                this.priseUAH = prise * currencyParser.getUsdRate();
                this.priseRUB = this.priseUAH / currencyParser.getRubRate();
                break;
            case RUB:
                this.priseRUB = prise;
                this.priseUAH = prise * currencyParser.getRubRate();
                this.priseUSD = this.priseUAH / currencyParser.getUsdRate();
                break;
            case UAH:
                this.priseUAH = prise;
                this.priseUSD = prise / currencyParser.getUsdRate();
                this.priseRUB = this.priseUAH / currencyParser.getRubRate();
                break;
        }
    }

    public String getPriseUSD() {
        return formatDouble(this.priseUSD) + " " + USD;
    }

    public String getPriseRUB() {
        return formatDouble(this.priseRUB) + " " + RUB;
    }

    public String getPriseUAH() {
        return formatDouble(this.priseUAH) + " " + UAH; }

    private String formatDouble(double prise) {
        DecimalFormat df = new DecimalFormat(PRICE_FORMAT);
        return df.format(prise);
    }
}
