package com.mk.parsers;

import com.mk.PreferenceSettingsManager;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

public class CurrencyParser {

    private static final String CURRENCY_JSON_URL = "http://resources.finance.ua/ru/public/currency-cash.json";
    private static final String UTF_8 = "UTF-8";
    private static final String JSON_ARRAY_ORGANIZATIONS = "organizations";
    private static final String JSON_OBJECT_CURRENCIES = "currencies";
    private static final String JSON_OBJECT_USD = "USD";
    private static final String JSON_OBJECT_RUB = "RUB";
    private static final String KEY_BID = "bid";
    private static final String KEY_TITLE = "title";
    private static final String PRIVAT_BANK = "ПриватБанк";
    private static final int ONE_DAY_IN_MS = 86400000;
    private double usdRate;
    private double rubRate;

    public CurrencyParser () {
        long currentDate = new Date().getTime();
        long lastUpdateCurrencyRateDate = PreferenceSettingsManager.getDateCurrencyUpdate();
        this.usdRate = PreferenceSettingsManager.getUsdRate();
        this.rubRate = PreferenceSettingsManager.getRubRate();
        if ((currentDate - lastUpdateCurrencyRateDate) > ONE_DAY_IN_MS) {
            try {
                JSONObject json = new JSONObject(IOUtils.toString(new URL(CURRENCY_JSON_URL), Charset.forName(UTF_8)));
                JSONArray organizations = json.getJSONArray(JSON_ARRAY_ORGANIZATIONS);
                for (int i = 0; i < organizations.length(); i++) {
                    JSONObject bank = organizations.getJSONObject(i);
                    if (bank.getString(KEY_TITLE).equals(PRIVAT_BANK)) {
                        JSONObject currencies = bank.getJSONObject(JSON_OBJECT_CURRENCIES);
                        JSONObject usd = currencies.getJSONObject(JSON_OBJECT_USD);
                        JSONObject rub = currencies.getJSONObject(JSON_OBJECT_RUB);
                        this.usdRate = usd.getDouble(KEY_BID);
                        this.rubRate = rub.getDouble(KEY_BID);
                        PreferenceSettingsManager.setUsdRate(this.usdRate);
                        PreferenceSettingsManager.setRubRate(this.rubRate);
                        PreferenceSettingsManager.setDateCurrencyUpdate(currentDate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getUsdRate() {
        return usdRate;
    }

    public double getRubRate() {
        return rubRate;
    }
}
